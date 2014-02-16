<?php
class ADERequester {
	
	private $sessionID;
	private $current_project;
	private $is_connected;
	
	private static $iq_ade_xml = null;
	private static $iut_ade_xml = null;
	
	public function __construct() {
		$this->is_connected = false;
		if (is_null(self::$iq_ade_xml)) {
			self::$iq_ade_xml = simplexml_load_file('resources.xml');
		}
		if (is_null(self::$iut_ade_xml)) {
			self::$iut_ade_xml = simplexml_load_file('resources_iut.xml');
		}
	}
	
	public function connect(){
		if (!$this->is_connected) {
			$url = ADE_SERVER_BASE_URL.'connect&login='.ADE_SERVER_LOGIN."&password=".ADE_SERVER_PASSWORD;

			$rep = $this->exec_query($url);
			$id_pattern = 'id="';
			
			if (empty($rep)) echo "Error during the connection";
			$this->sessionID = substr($rep, strpos($rep, $id_pattern) + strlen($id_pattern), 11);
			$this->is_connected = true;
		}
	}
	public function disconnect(){
		if ($this->is_connected) {
			$url = ADE_SERVER_BASE_URL.'disconnect&sessionId='.$this->sessionID;
			$this->exec_query($url);
		}
		$this->is_connected = false;
	}
	public function setProject($project){
		//if (!$this->is_connected) $this->connect();
		if($this->current_project != $project){
			$url = ADE_SERVER_BASE_URL."setProject&projectId=$project&sessionId=".$this->sessionID;
			$this->exec_query($url);
		}
		
		$this->current_project = $project;
	}
	public function getResourceByGroup($uri) {
		$id;
		
		if (!is_connected) {
			throw new Exception("You must be connect and have a valid project set.");
		}
		if (strpos($uri, '|') != -1) {
			//On passe le groupe et le demi groupe sous la forme group|half_group 
			$params = explode('|', $uri);
			$id = -1;
			
			foreach(self::$iq_ade_xml->semester as $semester) {
				foreach($semester->group as $group) {
					if ($group['id'] == $params[0]) {
						$id = $group->half_group[$params[1]-1];
					}
				}
			}
		}else {
			throw new Exception('getResourceByGroup parameter isn\'t well formed');
		}
		
		
		return $id;		
	}
	public function getImageData($screenW, $screenH, $resource, $date) {
		if (!is_connected) connect();
		
		$week = $this->parse_date($date);
		$resource_id = 0;
		
		//On regarde si le paramètre $resource est de la forme group|half_group, si oui on récupère l'id du demi-groupe depuis ADE, Sinon on suppose que c'est un id valide
		if(strpos($resource, "|") == false){
			$resource_id = $resource;
		}else{
			$resource_id = $this->getResourceByGroup($resource);
		}
		if ($resource_id == -1) throw new Exception('Bad resources parameter, must be > 0');
		
		$url = ADE_SERVER_BASE_URL.'imageET&sessionId='.$this->sessionID."&width=$screenW&height=$screenH&resources=$resource_id&weeks=$week";
		$rawdata = $this->exec_query($url);
		
		return $rawdata;
	}
	
	/**
	* Récupère le planning pour un prof identifié par son NOM + Prénom
	*/
	public function getResourceByName($name) {	
		if(!$this->is_connected){
			$this->connect();
			$this->setProject(28);
		}
		$url = ADE_SERVER_BASE_URL."getResources&category=instructor&name=$name&sessionId=".$this->sessionID;
		$xml = $this->exec_query(str_replace(" ","%20",$url));
		
		preg_match('/id="(.*)"/', $xml, $match);
		if(count($match) > 0){
			return $match[1];
		}else{
			return "";
		}
	}
	public function getEvents($resource, $end) {	
		global $sessionId;
		
		$resource_id = "";
		if (strpos($resource, "|") !== false) {
			$resource_id = $this->getResourceByGroup($resource);
		}else {
			$resource_id = $this->getResourceByName($resource);
		}
		$startDate = strftime('%m/%d/%Y', time());
		$endDate = strftime('%m/%d/%Y', $end/1000); // Le timestamp java est 1000 fois supérieur à ceui de PhP
		$url = ADE_SERVER_BASE_URL.'getEvents&resources='.$resource_id.'&sessionId='.$this->sessionID.'&detail=8&startDate='.$startDate.'&endDate='.$endDate;
		
		return $this->exec_query($url);
	}
	
	public function getSchoolYearDateStart() {
	
		//On demande à ADE le timestamp de la première semaine du planning
		date_default_timezone_set('UTC');
		$firstWeek = $this->getDate(1, 0, 0);
		
		return $firstWeek;
	}
	public function getDate($slot, $day, $week) {
		global $ADE_BASE_URL, $sessionId;
		
		$url = ADE_SERVER_BASE_URL.'getDate&sessionId='.$this->sessionID."&week=$week&day=$day&slot=$slot";
		$rep = $this->exec_query($url);
		$firstWeek = date_create();
		
		if (!empty($rep)) {
			//Le timestamp est contenu dans l'attribut 'time'
			$pattern = '/time="(.*?)"/';
			preg_match($pattern, $rep, $matches);
			
			if (count($matches) > 1) {
				//Si l'expression régulière match, on récupère le groupe 1
				$timestamp = $matches[1];
				
				//Parfois le parsing revoie un timestamp de plus de 10 caractères, on limite donc à 10 caractères la chaine. Need improvement.
				if (strlen($timestamp) > 10) $timestamp = substr($timestamp, 0, 10);
				
				$firstWeek = $timestamp;
			}
		}
		
		return $firstWeek;
	}
	private function parse_date($date) {
		global $ADE_BASE_URL, $sessionId;
		
		//On demande à ADE le timestamp de la première semaine du planning
		$url = ADE_SERVER_BASE_URL.'getDate&sessionId='.$this->sessionID.'&week=0&day=0&slot=1';
		$rep = $this->exec_query($url);
		
		date_default_timezone_set('UTC');
			
		//Récupération de la date de rentrée
		$firstWeek = $this->getSchoolYearDateStart();
		
		//Si le paramètre est now pour la date, on récupère la date actuelle (timestamp)
		if($date == 'now'){
			$askedDate = time();
		}
		else {
			//Sinon on récupère le timestamp passé en params
			$askedDate = $date;
		}
		
		if(isset($_REQUEST['gap'])){
			//Decalage par rapport à "now" 
			$weekGap = intval($_REQUEST['gap'], 10);
			$askedDate += ($weekGap * (7 * 24 * 60 * 60));
		}
		
		//On fait la différence entre la date demandée et la date référence pour ADE 
		$interval = $this->date_diff($askedDate, $firstWeek);
		$interval = $interval['days'] / 7;
		//Et on récupère la différence en nombre de semaines
		return (int)$interval;
	}
	private function exec_query($url){
		//echo "Excuting request on : $url </br>"; //DEBUG
		$c = curl_init();
		curl_setopt($c,CURLOPT_URL, $url);
		curl_setopt($c,CURLOPT_RETURNTRANSFER, TRUE);
		$response = curl_exec($c);
		if(curl_errno($c)){
			return 'Curl error: ' . curl_error($c);
		}
		curl_close($c);
		return $response;
	}
	
	/* Les fonctions ci dessous ont été mportées du code source de PhP 5.3 */
	private function date_diff($one, $two)
	{
		$invert = false;
		if ($one > $two) {
			list($one, $two) = array($two, $one);
			$invert = true;
		}

		$key = array("y", "m", "d", "h", "i", "s");
		$a = array_combine($key, array_map("intval", explode(" ", date("Y m d H i s", $one))));
		$b = array_combine($key, array_map("intval", explode(" ", date("Y m d H i s", $two))));

		$result = array();
		$result["y"] = $b["y"] - $a["y"];
		$result["m"] = $b["m"] - $a["m"];
		$result["d"] = $b["d"] - $a["d"];
		$result["h"] = $b["h"] - $a["h"];
		$result["i"] = $b["i"] - $a["i"];
		$result["s"] = $b["s"] - $a["s"];
		$result["invert"] = $invert ? 1 : 0;
		$result["days"] = intval(abs(($one - $two)/86400));

		if ($invert) {
			$this->_date_normalize(&$a, &$result);
		} else {
			$this->_date_normalize(&$b, &$result);
		}

		return $result;
	}
	/* Importé du code source de PhP 5.3 */
	private function _date_range_limit($start, $end, $adj, $a, $b, $result)
	{
		if ($result[$a] < $start) {
			$result[$b] -= intval(($start - $result[$a] - 1) / $adj) + 1;
			$result[$a] += $adj * intval(($start - $result[$a] - 1) / $adj + 1);
		}

		if ($result[$a] >= $end) {
			$result[$b] += intval($result[$a] / $adj);
			$result[$a] -= $adj * intval($result[$a] / $adj);
		}

		return $result;
	}
	/* Importé du code source de PhP 5.3 */
	private function _date_range_limit_days($base, $result)
	{
		$days_in_month_leap = array(31, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		$days_in_month = array(31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

		$this->_date_range_limit(1, 13, 12, "m", "y", &$base);

		$year = $base["y"];
		$month = $base["m"];

		if (!$result["invert"]) {
			while ($result["d"] < 0) {
				$month--;
				if ($month < 1) {
					$month += 12;
					$year--;
				}

				$leapyear = $year % 400 == 0 || ($year % 100 != 0 && $year % 4 == 0);
				$days = $leapyear ? $days_in_month_leap[$month] : $days_in_month[$month];

				$result["d"] += $days;
				$result["m"]--;
			}
		} else {
			while ($result["d"] < 0) {
				$leapyear = $year % 400 == 0 || ($year % 100 != 0 && $year % 4 == 0);
				$days = $leapyear ? $days_in_month_leap[$month] : $days_in_month[$month];

				$result["d"] += $days;
				$result["m"]--;

				$month++;
				if ($month > 12) {
					$month -= 12;
					$year++;
				}
			}
		}

		return $result;
	}
	/* Importé du code source de PhP 5.3 */
	private function _date_normalize($base, $result)
	{
		$result = $this->_date_range_limit(0, 60, 60, "s", "i", $result);
		$result = $this->_date_range_limit(0, 60, 60, "i", "h", $result);
		$result = $this->_date_range_limit(0, 24, 24, "h", "d", $result);
		$result = $this->_date_range_limit(0, 12, 12, "m", "y", $result);

		$result = $this->_date_range_limit_days(&$base, &$result);

		$result = $this->_date_range_limit(0, 12, 12, "m", "y", $result);

		return $result;
	}
}
?>
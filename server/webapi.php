<?php
	require_once('ApplicationConstants.php');
	require_once('IUTLoginHelper.php');
	require_once('ADERequester.php');
	require_once('SchoolingDataManager.php');
	require_once('ICalendarGenerator.php');
	
	function parse_function(){
		if (!isset($_REQUEST['function'])) throw_error("Impossible de déterminer la fonction demandée, paramètre function manquant.");
		
		/*if (strtolower($_REQUEST['function']) != 'connect') {
			if (!check_sessionID()) {
				throw_error('You have to be connect to use this webapi.');
			}
		}*/
		
		switch(strtolower($_REQUEST['function'])) {
			case 'getplanning' : {
				getPlanning();
				break;
			}
			case 'getabsences' : {
				getAbsences();
				break;
			}
			case 'getmarks' : {
				getMarks();
				break;
			}
			case 'getresourceid' : {
				getResourceId();
				break;
			}
			case 'generateresourcesxml' : {
				generateResourcesXML();
				break;
			}
			case 'connect' : {
				connect();
				break;
			}
			case 'disconnect' : {
				disconnect();
				break;
			}
			case 'geticsplanning' : {
				generateICalendarFile();
				break;
			}
			default : {
				throw_error("La fonction passée en paramètre est inconnue.");
			}
		}
	}
	function checkSessionID() {
		return $_SESSION[$_REQUEST['sessionId']] == true;
	}
	function generateICalendarFile() {
		$params = array('resource', 'end');
		if (check_args_isset($params)) {
			echo ICalendarGenerator::fromADE($_REQUEST['resource'], $_REQUEST['end']);
		}
	}
	function generateResourcesXML() {
		$params = array('project');
		if (check_args_isset($params)) {
			$id = -1;
			if (isset($_REQUEST['resources'])) {
				//Paramètre facultatif, si omis génère le xml pour toutes les ressources du projet.
				$id = $_REQUEST['resources'];
			}
			try {
				$ade = new ADERequester;
				$ade->connect();
				$ade->setProject($_REQUEST['project']);
				$ade->getResources(new ADEResourcesParser, $id);
				$ade->disconnect();
			}catch (Exception $e) {
				throw_error($e->getMessage());
			}
		}
	}
	function getPlanning() {
		$params = array('screenW', 'screenH', 'date', 'resources', 'project');
		if (check_args_isset($params)) {
			try {
				$ade = new ADERequester;
				$ade->connect();
				$ade->setProject($_REQUEST['project']);
				$image = $ade->getImageData($_REQUEST['screenW'], $_REQUEST['screenH'], $_REQUEST['resources'], $_REQUEST['date']);
				echo $image;
			}catch (Exception $e) {
				throw_error($e->getMessage());
			}
		}else {
			exit(1);
		}
	}
	
	function getAbsences() {
		$params = array('id');
		if (check_args_isset($params)) {
			try {
				$absences = new SchoolingDataManager;
				echo $absences->getAbs($_REQUEST['id']);
			}catch (Exception $e) {
				throw_error($e->getMessage());
			}
		}else {
			exit(1);
		}
	}
	
	function getMarks() {
		$params = array('id');
		if (check_args_isset($params)) {
			try {
				$marks = new SchoolingDataManager;
				header("Content-type : text/xml");
				echo $marks->getMarks($_REQUEST['id']);
			}catch (Exception $e) {
				throw_error($e->getMessage());
			}
		}else {
			exit(1);
		}
	}
	
	function getResourceId() {
		
	}
	function connect() {
		$params = array('login', 'password');
		if (check_args_isset($params)) {
			try {
				$iut = new IUTLoginHelper();
				$result = $iut->connect($_REQUEST['login'], $_REQUEST['password']);
				
				header("Content-type : text/xml");
				echo $result;
			}catch (Exception $e) {
				throw_error($e->getMessage());
			}
		}else {
			exit(1);
		}
	}
	function disconnect() {
	
	}
	
	function check_args_isset($params) {
		$missing_params = array();
		$result = true;
		foreach($params as $param) {
			if (!isset($_REQUEST[$param])) {
				array_push($missing_params, $param);
			}
		}
		if (!empty($missing_params)) {
			args_isset_error($missing_params);
			$result = false;
		}
		return $result;
	}
	function args_isset_error($params) {
		$error = 'Missing parameter';
		$error .= count($params) > 1 ? 's : ' : ' : ';
		foreach($params as $param) {
			$error.= "$param, ";
		}
		$error = substr($error, 0, strlen($error) - 2);
		echo $error;
	}
	function throw_error($message) {
		header("Content-type : html/text");
		die("$message</br>");
	}	
	
	$SERVER_HOST = $_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']);

	if (strcmp($SERVER_HOST,DEBUG_SERVER_ADRRESS)) {
		define('SERVER_CONFIGURATION', DEBUG_MODE);
	}else {
		define('SERVER_CONFIGURATION', RELEASE_MODE);
	}
	
	parse_function();
?>
<?php
require_once('IBaseConnection.php');
require_once('Mysql.php');
require_once('IUTLoginHelper.php');
require_once('ApplicationConstants.php');

class SchoolingDataManager{	

	define('SELECT_CURRENT_DATE', 'SELECT NUM, DATEDEBUT, DATEFIN FROM ANNEEU WHERE CURRENT_DATE between ANNEEU.DATEDEBUT AND ANNEEU.DATEFIN' );
	
	define('SELECT_MARKS', 'SELECT matieres.LIBELLE AS matiere, note.NOTE as note, examen.COEF as coefficient, examen.DATEEXAM as jour, examen.COMMENTAIRE as info 
		FROM note, examen, matieres 
		WHERE note.IDEXAMEN = examen.NUM 
		AND examen.IDMATIERE2 = matieres.NUM 
		AND IDETUDIANT=\'%s\'
		AND examen.DATEEXAM BETWEEN \'%s\' AND \'%s\'
		ORDER BY matieres.LIBELLE');
		
	define('SELECT_ABSENCES', 'SELECT DATEABSENCE AS jour, LIBELLE AS matiere, HEUREDEBUT AS heure, DUREEHEURES AS duree
		FROM matieres, absences 
		WHERE absences.IDMATIERE2 = matieres.NUM 
		AND absences.IDETUDIANT = \'%s\'
		AND absences.DATEABSENCE BETWEEN (SELECT DATEDEBUT 
									FROM anneeu 
									WHERE NUM=%s)
							AND (SELECT DATEFIN
								FROM anneeu
								WHERE NUM=$idYear)
		ORDER BY DATEABSENCE DESC');

	private $connect;
	
	public function __construct() {
		if(SERVER_CONFIGURATION == RELEASE_MODE){
			$this->connect = new IBaseConnection(RELEASE_BDD_SERVER, RELEASE_BDD_SERVER_FILE, RELEASE_BDD_LOGIN, RELEASE_BDD_PASSWORD);
		}else if(SERVER_CONFIGURATION == DEBUG_MODE){
			$this->connect = new Mysql(DEBUG_BDD_SERVER, DEBUG_BDD_SERVER_FILE, DEBUG_BDD_LOGIN, DEBUG_BDD_PASSWORD);
		}
	}
        
	public function getLastYear() {
		$result = $this->connect->TabResSQL(SELECT_CURRENT_DATE);
		return $result;
	}
    public function getMarks($id) {
		$yearInfos = $this->getLastYear();
		
		$idYear = $yearInfos[0]['NUM'];
		$dateStart = $yearInfos[0]['DATEDEBUT'];
		$dateEnd = $yearInfos[0]['DATEFIN'];
		
		$result = $this->connect->TabResSQL(sprintf(SELECT_MARKS, $id, $dateStart, $dateEnd));
		echo $this->generate_xml_response($result, 'mark');
	}
				
	public function getAbs($id) {
		$idYear = $this->getLastYear();
		$result = $this->connect->TabResSQL(sprintf(SELECT_ABSENCES, $id, $idYear));
		return $this->generate_xml_response($result, 'absence');
	}
		
	private function generate_xml_response($data, $node_name){
		$xml = new DOMDocument('1.0', 'UTF-8');
		$root = $xml->createElement('data', '');
		foreach($data as $index) {
			$node = $xml->createElement(utf8_encode($node_name), "");
			foreach($index as $key => $value) {
				$node->appendChild($xml->createElement(utf8_encode(strtolower($key)), utf8_encode($value)));
			}
			$root->appendChild($node);
		}
		$xml->appendChild($root);
		return $xml->saveXML();
	}
}
?>
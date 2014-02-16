<?php
require_once('IBaseConnection.php');
require_once('Mysql.php');
require_once('LdapHelper.php');
require_once('ADERequester.php');
require_once('ApplicationConstants.php');


class IUTLoginHelper{
	
	const STUDENT_ACCOUNT_TYPE = "student";
	const TEACHER_ACCOUNT_TYPE = "teacher";
	const CONNECTED_USER_CONNECTION = "true";
	const RESTRICTED_USER_CONNECTION = "restricted";   

	function __constructor() {

	}
	function connect($login, $password, $project = 28) {
		$result;

		$ldap_filters = "(&(objectClass=user)(samaccountname=$login))";

		// Beaucoup d'infos superflux / récupérer dans la BDD après $ldap_wanted_attributes = array ("cn", "samaccountname", "displayname", "sn", "givenName", "mail", "title", "department");
		$ldap_wanted_attributes = array ('*');

		//On instancie une connexion vers la base suivant le serveur hote utilisé 
		$db = null;
		
		//Connexion sur la base de l'IUT en prod
		if(SERVER_CONFIGURATION == RELEASE_MODE){
			$db = new IBaseConnection(RELEASE_BDD_SERVER, RELEASE_BDD_SERVER_FILE, RELEASE_BDD_LOGIN, RELEASE_BDD_PASSWORD);
		}else if(SERVER_CONFIGURATION == DEBUG_MODE){ //Connexion sur la base de test, en debug
			$db = new Mysql(DEBUG_BDD_SERVER, DEBUG_BDD_SERVER_FILE, DEBUG_BDD_LOGIN, DEBUG_BDD_PASSWORD);
		}
		
		//On instancie une connexion vers le serveur LDAP de l'IUT
		$ldap = new LdapHelper(LDAP_SERVER, LDAP_PORT, LDAP_DOMAIN, LDAP_VERSION);
		$ldap->connect();

		//On tente le bind sur le LDAP
		if (!$ldap->bind($login, $password)) {
			//La paire login/mdp n'est pas acceptée par le LDAP, on refuse la connexion, et on en informe le client
			$result = array(array());
			$result[0]['connected'] = 'false';
			$result[0]['reason'] = 'Unknow account or wrong password';
			return $this->generate_xml_response($result);
		}
		//On récupère les attributs pour l'utilisateur
		$entry = $ldap->getAttributes(LDAP_BASE_DN, $ldap_filters, $ldap_wanted_attributes);
		
		//Récupération de la dernière connexion de l'utilisateur
		$lastIUTLogon = $entry['lastLogon'][0];
		
		//On se déconnecte du serveur LDAP
		$ldap->disconnect();

		//Le ldap_id correspond au numéro étudiant ( le container dans le LDAP ) ou l'id professeur
		$ldap_id = $entry['cn'][0];
		//Le ldap_type correspond au type d'utilisateur Etudiant/Professeur
		$ldap_type = $entry['title'][0];

		//On récupère les informations en BDD, puis on genère le XML de sortie.
		try{
			if ($ldap_type == 'Etudiant') {
				$result = $db->TabResSQL("SELECT NUM,NOM, PRENOM, IDDEPARTEMENT, IDGROUPE, DEMIGROUPE, MAIL FROM etudiants WHERE NUM ='$ldap_id'");
				$result[0]['connected'] = self::CONNECTED_USER_CONNECTION;
				$result[0]['type'] = self::STUDENT_ACCOUNT_TYPE;
			}else {
				$result = $db->TabResSQL("SELECT NOM, PRENOM, MAIL FROM profs WHERE LOGIN='$login'");
				$result[0]['connected'] = self::CONNECTED_USER_CONNECTION;
				$result[0]['type'] = self::TEACHER_ACCOUNT_TYPE;

				//On demande à ADE de récupérer l'id du professeur sur le planning en passant le nom et prénom du-dit prof.
				$ade = new ADERequester();
				$result[0]['num'] = $ade->getResourceByName($result[0]['NOM'].' '.$result[0]['PRENOM']);
			}
			$result[0]['sessionKey'] = empty($lastIUTLogon) ? $this->generate_session_key(0) : $this->generate_session_key($lastIUTLogon);
		}catch (Exception $e) {
			//Si le resultat est toujours null, les id de connexion ne correspondent ni à un etudiant, ni à un prof, on accepte la connexion
			//Cependant cette connexion est restreinte, et l'utilisateur n'aura pas accès à certains services sur l'application
			
			//Le type de l'utilisateur est soit 'Etudiant', soit 'Enseignant X', X représentant un département dans lequel l'enseignant est amené à donner des cours
			//D'où l'utilisation d'un strpos() le suffixe n'étant pas fixe pour ce type de ressource.
				
			if ($ldap_type == 'Etudiant' || strpos($ldap_type, 'Enseignant') !== false) {
				//La connexion est restreinte
				$result[0]['connected'] = self::RESTRICTED_USER_CONNECTION;
				$result[0]['type'] = strtolower($ldap_type) == 'etudiant' ? self::STUDENT_ACCOUNT_TYPE : self::TEACHER_ACCOUNT_TYPE;
				$result[0]['nom'] = $entry['sn'][0];
				$result[0]['prenom'] = $entry['givenName'][0];
				$result[0]['num'] = $entry['cn'][0];
				$result[0]['mail'] = $entry['userPrincipalName'][0];
				$result[0]['sessionKey'] = empty($lastIUTLogon) ? $this->generate_session_key(0) : $this->generate_session_key($lastIUTLogon);

				//Si le département de l'étudiant contient des sous département (IQ/IQ1) par exemple, on ne récupère que l'intitulé du cursus afin de faire le bind avec ADE
				//(Seule cette première partie, correspondant à l'intitulé du cursus est concordant avec les ressources ADE).
				if (strpos($entry['department'][0], "/") != -1) {
					//On récupère la partie avant le '/'
					$dep = $entry['department'][0];
					$result[0]['iddepartement'] = substr($dep, 0, strpos($dep, "/"));
				}else {
					//Sinon on récupère tout l'intitulé
					$result[0]['iddepartement'] = $entry['department'][0];
				}
			}else {	
				//Si ce n'est pas un prof ou un étudiant on refuse la connexion pour éviter les problèmes
				$result[0]['connected'] = false;
				$result[0]['reason'] = "Invalid account type, permission denied";
			}
		}
		$db->close();
		//On crée le XML à partir du tableau $result
		return $this->generate_xml_response($result);
	}
	
	//Génération de la clé de cryptage pour l'application
	function generate_session_key($salt){
		$key = hash('md5', time()+intval($salt, false));
		$key = hash('sha512', $key, false);
		return $key;
	}
	
	function generate_xml_response($data){
		$xml = new DOMDocument('1.0');
		$root = $xml->createElement('user', '');
		foreach($data as $index) {
			foreach($index as $key => $value) {
				$root->appendChild($xml->createElement(strtolower($key), str_replace(" ","",$value)));
			}
		}
		$xml->appendChild($root);
		return $xml->saveXML();
	}
}
?>
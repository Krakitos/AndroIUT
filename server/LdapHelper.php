<?php
class LdapHelper {
	
	const DEFAULT_LDAP_PORT = '389';
	const DEFAULT_LDAP_SSL_PORT = '636';
	
	private $server;
	private $port;
	private $version;
	private $ldap_session;
	private $domain;
	/**
	* Créer une instance LDAPHelper
	*@params $server Correspond à l'url ou l'IP du serveur qui héberge le LDAP
	*@params $port Port du serveur pour la connexion LDAP, -1 pour l'utilisation des valeurs par défaut 389/636
	*@pamras $ssl Connexion SSL ?
	*@params $version Spécifie la version LDAP utilisée sur le serveur
	*/
	
	public function __construct($server, $port = -1, $domain="", $version = 3, $ssl = false) {
		if (!filter_var($server, FILTER_VALIDATE_URL) && !filter_var($server, FILTER_VALIDATE_IP)) throw new Exception("Parameter server have to have a valid value");
		
		if ($port < 0) {
			$port = $ssl ? self::DEFAULT_LDAP_SSL_PORT : self::DEFAULT_LDAP_PORT;
		}
		
		$this->server = $server;
		$this->port = (int)$port;
		$this->version = intval($version);
		$this->domain = $domain;
	}
	/**
	* Essaye de se connecter au serveur LDAP.
	* @throw Exception Echec de la connexion au serveur
	*/
	public function connect() {
		if(!$this->ldap_session){
			$this->ldap_session = ldap_connect($this->server, $this->port);
			if (!$this->ldap_session) {
				throw new Exception("Unable to connect on the server".$this->server.":".$this->port);
			}
			$this->setOptions();
		}
	}
	/**
	* Vérifie si le nom et le mot de passe sont connu et corrects dans l'AD
	* @params $login Le nom d'utilisateur pour la connexion
	* @params $password Le mot de passe associé à l'utilisateur
	* @return True si l'authentification est réussie, false sinon.
	*/
	
	public function bind($login, $password) {
		$binded = false;
		$binded = @ldap_bind($this->ldap_session, $this->domain.'\\'.$login, $password);
		return $binded;
	}
	
	/**
	* Récupère les attributs d'une recherche exécutée sur l'AD.
	* @params $base_dn Base DN pour le dossier
	* @params $filter Filtre de recherche
	* @params $attributes Les attributs que l'on souhaite récupérer 
	* @return array() Un tableau contenant les attributs demandé si au moins un éléments est retourné par le filtre
	*/
	
	public function getAttributes($base_dn, $filters, $attributes) {
		if (!is_array($attributes)) $attributes = array($attributes);
		
		if (!$this->ldap_session) connect();
		$sr = ldap_search($this->ldap_session, $base_dn, $filters, $attributes);
		$entry = ldap_first_entry($this->ldap_session, $sr);
		
		return ldap_get_attributes($this->ldap_session, $entry);
	}
	/**
	* Met fin à une session LDAP avec le serveur
	*/
	public function disconnect() {
		if ($this->ldap_session) {
			ldap_unbind($this->ldap_session);
			$this->ldap_session = null;
		}
	}
	/**
	* Défini les options de version et de droits sur le serveur
	*/
	private function setOptions() {
		ldap_set_option($this->ldap_session, LDAP_OPT_PROTOCOL_VERSION, $this->version);
		ldap_set_option($this->ldap_session, LDAP_OPT_REFERRALS, 0);
	}
}
?>
<?php
 
  class IBaseConnection
  {
    private
      $Serveur     = '',
      $Bdd         = '',
      $Identifiant = '',
      $Mdp         = '',
      $Lien        = '',  
      $Debogue     = true,  
      $NbRequetes  = 0;
 
    /**
    * Constructeur de la classe
    * Connexion aux serveur de base de donnée et sélection de la base
    *
    * $Serveur     = L'hôte (ordinateur sur lequel Mysql est installé)
    * $Bdd         = Le nom de la base de données
    * $Identifiant = Le nom d'utilisateur
    * $Mdp         = Le mot de passe
    */ 
    public function __construct($Serveur = 'localhost', $Bdd = 'base', $Identifiant = 'root', $Mdp = '') 
    {
      $this->Serveur     = $Serveur;
      $this->Bdd         = $Bdd;
      $this->Identifiant = $Identifiant;
      $this->Mdp         = $Mdp;
	  $url = $Serveur.':'.$Bdd;
      $this->Lien = ibase_pconnect($url, $this->Identifiant, $this->Mdp);
		if($this->Lien==null) die("Aucune connexion");
  //    ibase_query($this->Lien, "USE $Bdd");
    }
 
    /**
    * Retourne le nombre de requêtes SQL effectué par l'objet
    */     
    public function RetourneNbRequetes() 
    {
      return $this->NbRequetes;
    }
 
    /**
    * Envoie une requête SQL et récupère le résultât dans un tableau pré formaté
    *
    * $Requete = Requête SQL
    */ 
    public function TabResSQL($Requete)
    {
      $i = 0;
	   $url = $this->Serveur.':'.$this->Bdd;
      $this->Lien = ibase_pconnect($url, $this->Identifiant, $this->Mdp);		
      $Ressource = ibase_query($this->Lien,$Requete);
	  if($Ressource==null) die($Requete);	  
      $TabResultat=array();
      while ($Ligne = ibase_fetch_assoc($Ressource)){
        foreach ($Ligne as $clef => $valeur) $TabResultat[$i][$clef] = $valeur;
        $i++;
      }
	  
      ibase_free_result($Ressource);
      $this->NbRequetes++;
	  
	  if (empty($TabResultat)) throw new Exception("MySQL return empty result");
      return $TabResultat;
    }
	public function close() {
		ibase_close($this->Lien);
	}
	
    /**
    * Envoie une requête SQL et retourne le nombre de table affecté
    *
    * $Requete = Requête SQL
    */ 
    public function ExecuteSQL($Requete)
    {
      $Ressource = ibase_query($Requete,$this->Lien);

      $this->NbRequetes++;
      $NbAffectee = ibase_affected_rows();
	  
	  ibase_free_result($Ressource);
	  
      return $NbAffectee;      
    }    
  }
?>
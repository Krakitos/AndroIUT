<?php

class Logger{

	private static  $log_file = 'log.txt';
	private static $log_queue = array();
	
	public static function save($sender, $exception, $level = 'error'){
		$file = fopen(self::$log_file, 'a');
		
	}
	
}  
?>
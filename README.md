Jscp
===========

SCP utility to tar a folder, zip it, and scp it somewhere, then unzip it.

Usage:

		Rsync.sync("src/dir", 
				   "user", 
				   "host", 
				   "destination/path", 
				   Arrays.asList("exclude", "list"), // regex ignore list
				   new File("private/key"));

or: 

		Rsync.sync("src/dir", 
				   "user", 
				   "host", 
				   "destination/path", 
				   Arrays.asList("exclude", "list"), // regex ignore list
				   "password");
		
		

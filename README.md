remote-copy
===========

SCP utility to tar a folder, zip it, and scp it somewhere, then unzip it.

Usage:

        // create secure context
        SecureContext context = new SecureContext("userName", "localhost");
        
        // set optional security configurations.
        context.setTrustAllHosts(true);
        context.setPrivateKeyFile(new File("private/key"));

        // Console requires JDK 1.7
        // System.out.println("enter password:");
        // context.setPassword(System.console().readPassword());

		Jscp.exec(context, 
				   "src/dir",
				   "destination/path",
				   // regex ignore list 
				   Arrays.asList("logs/log[0-9]*.txt",
				   "backups") 
				   );

Also includes useful classes - Scp and Exec, and a TarAndGzip, which work in 
pretty much the same way.
		
		
class Controller 	{
	
	Serial controlPort;
	float val;
  
	Controller(PApplet appletName)	{
		controlPort = new Serial(appletName, "COM5", 9600);
	}

	int controllerRead()	{
		if(controlPort.available() > 0)	{
			val = controlPort.read();     
			println("val: "+val);
			return (int) val;
		}
		else {
			return (int) val;
		}
	}
}

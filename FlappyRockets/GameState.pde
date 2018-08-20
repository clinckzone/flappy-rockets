class GameState		{
  	ArrayList<Obstacle> obstacles;
 	ArrayList<Obstacle> collidedObjects;  //An arraylist to store the asteroids that have collided with the rocket       

  	int mode;
  	int score;
  	int lives;

  	PImage backgroundImg;
	PImage rocketImage;
  	SoundFile soundFile;
	PFont scoreFont;

	GameState(PApplet program)		{
    	obstacles = new ArrayList<Obstacle>();
    	collidedObjects = new ArrayList<Obstacle>();
    	
    	mode  = -1;
    	score = 0;
    	lives = 5;

    	//background
    	backgroundImg = loadImage("background.png");
    	backgroundImg.resize(width, height);
    	 
		rocketImage = loadImage("./rocket/rocketLife.png");
		scoreFont = createFont("font/Moon Light.otf", 50);
		soundFile = new SoundFile(program, "./music/sound.wav"); 
	}

	void display()	{
		for(int i = 0; i < lives; i++)	{
			imageMode(CENTER);
			image(rocketImage, 50*(i+1), 40, 125, 125);
		}
		textFont(scoreFont);
		text(score, width - 75, height -25);
	}
}
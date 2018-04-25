package Simulation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestMain 
{

	public static void main (String[] argv)
	{
		Properties prop = new Properties();
		InputStream input = null;

		try 
		{
			input = new FileInputStream("Simulation.prop");
			prop.load(input);
						
		} catch (IOException ex) 
		{
			System.out.println(ex.toString());;
		}
		int totalSteps = 10000;
		double areaWidth = Double.parseDouble( prop.getProperty("areaWidth"));
		double areaHeight = Double.parseDouble( prop.getProperty("areaHeight"));
		int numSensor =Integer.parseInt(prop.getProperty("numSensor"));
		int numTarget = Integer.parseInt(prop.getProperty("numTarget"));
		double coverRange = Double.parseDouble( prop.getProperty("coverRange"));
		double radioError=Double.parseDouble( prop.getProperty("radioError"));
		double maxVeclocity=Double.parseDouble( prop.getProperty("maxVeclocity"));
		int numParticles =  Integer.parseInt(prop.getProperty("sampleNum"));
		
		MSTSimulator sim = new MSTSimulator( areaWidth,  areaHeight,  numSensor,  numTarget,  coverRange,  radioError,  maxVeclocity,  totalSteps, numParticles);
		sim.init();
		sim.runSimulation();
		
	}
	
}

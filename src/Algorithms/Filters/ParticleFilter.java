// Base class of Particle Filter Estimator 
// Implemented according to "http://www.kev-smith.com/teaching/L7_ParticleFilters.pdf"
// Let x be the underlying state (i.e ground truth)
// Let z be the observation, such as RSSI measurement 
// PF tries to estimate p( x_{t+1}|z_{t+1} ) ( i.e. the current location given observation z_{t+1} )
// p( x_{t+1} | z_{t+1} ) \propto  p( z(t+1) | x_{t+1} ) p( x_{t+1} | x_{t} ) p( x_{t}|z_{t} )
// Estimation is done according to the above
// p( x_{t}|z_{t} ) is the estimated location at late time instance
// p( x_{t+1} | x_{t} ) is computed according to application specific motion model, such as random walk in a circular area
// p( z(t+1) | x_{t+1} )  is computed according to the observation model, such as log-distance model/trained RSSI MAP

package Algorithms.Filters;
import java.util.*;

import Common.TwoDPoint;
import Common.Utils;


public class ParticleFilter 
{
	
	public ParticleFilter (double areaWidth, double areaHeight, IObservationModel observer, IMotionModel predictor)
	{
		this.areaHeight =areaHeight;
		this.areaWidth = areaWidth;
		this.observer = observer;
		this.predictor = predictor;
		particles = new ArrayList<Particle>();
	}
	
	
	
	public void CreateInitialParticles (int num)
	{
		/*
		if (particles==null)
		{
			throw new NullPointerException("The provided collection must not be null.");
		}
		*/
		/* create num particles uniformly in the site
		 */
		for(int n = 0; n <num; n++)
		{
			Particle p = new Particle(Utils.UniformDist(0.0, areaWidth),  Utils.UniformDist(0.0, areaHeight), 1.0/num );
			particles.add(p);
		}
			
	}
	
	
	/*
	 Draws particles randomly where particles which have higher weight have greater probability to be drawn. 
	 A single particle can be chosen more than once.
	*/
	
	public ArrayList<Particle> draw (int sampleCount)
	{
		
	    /*************** calculate cumulative weights ****************/
        double[] cumulativeWeights = new double[particles.size()];
        int cumSumIdx = 0;
        double cumSum = 0;
        for (Particle p : particles)
        {
            cumSum += p.getWeight();
            cumulativeWeights[cumSumIdx++] = cumSum;
        }
		
        
        /* re-sample particles */
        double maxCumWeight = cumulativeWeights[particles.size() - 1];
        double minCumWeight = cumulativeWeights[0];
            
        ArrayList<Particle> drawnParticles = new ArrayList<Particle>();

        Random rand = new Random();
        

        for (int i = 0; i < sampleCount; i++)
        {
            double randWeight = minCumWeight + rand.nextDouble() * (maxCumWeight - minCumWeight);

            int particleIdx = 0;
            while (cumulativeWeights[particleIdx] < randWeight) //find particle's index
            {
                particleIdx++;
            }

           // Particle p = particles.get( particleIdx );
            Particle p = particles.get( particleIdx ).clone();

            drawnParticles.add(p);
        }
		
        
        
        return drawnParticles;
	}
	
	
	/*  Importance re-sampling */
	public ArrayList<Particle> reSampling ()
	{
        ArrayList<Particle> drawnParticles = draw(particles.size());
       
        
        double initialWeight = 1.0 / particles.size();
        
        for(Particle p: particles)
        {
        	p.setWeight(initialWeight);
        }
        
        this.particles = drawnParticles;
        return drawnParticles;
	}
	
	
	/*  Importance re-sampling */
	/* Keep re-sampling until the effectiveCountRatio is meet*/
	public ArrayList<Particle> reSampling (double minEffectiveCountRatio)
	{

        double  effectiveCountRatio = effectiveCountRatio();
        while( effectiveCountRatio< minEffectiveCountRatio )
        {
        	  ArrayList<Particle> drawnParticles = draw(particles.size());
        	  this.particles = drawnParticles;
        	  effectiveCountRatio = effectiveCountRatio();
        }
           
        double initialWeight = 1.0 / particles.size();
        /* reset weight */
        for(Particle p: particles)
        {
        	p.setWeight(initialWeight);
        }
        
        return this.particles;
	}
	
	
   /**
    * Predict the local of each particle in the next time step according to
    * motion model
    * i.e.  p( x_{t+1} | x_{t} )
    */	
   public ArrayList<Particle> predict (double timeInterval)
   {
	   return predictor.Predict(particles, timeInterval);
	   //return particles.Predict(effectiveCountMinRatio, particles.size());
   }
   
   
   /**
    * Correct our prediction with observation
    * i.e. p( z(t+1) | x_{t+1} )
    */	
   public ArrayList<Particle> correct ()
   {
	   observer.measure(particles);
	   Collections.sort(particles);   
	   return this.particles;
   }
   
   
   public double effectiveCountRatio()
   {
	   //double count = (double)effectiveParticleCount(getNormalizedWeights());
	   double count = (double)effectiveParticleCount();
	   return count / particles.size();
   }
   
   
	
   public double effectiveParticleCount(ArrayList<Double> weights)
   {
       double sumSqr = 0.0;
       //double sum = 0.0;
       for(Double w : weights)
       {
    	   sumSqr += w*w;
    	   //sum += w;
       }
       
       //return sumSqr==0 ? 0:sum / sumSqr;
       return sumSqr==0 ? 0 : 1.0/sumSqr;
   }
   
   
   public double effectiveParticleCount()
   {
       double sumSqr = 0.0;
       //double sum = 0.0;
       for(Particle p: particles)
       {
    	   
    	   sumSqr += p.getWeight()*p.getWeight();
    	   
       }   
       //return sumSqr==0 ? 0:sum / sumSqr;
       return sumSqr==0 ? 0 : 1.0/sumSqr;
   }

   
	public void normalizeWeights()
	{
		double weightSum = 0.0;
		for(Particle p:particles)
		{
			weightSum +=p.getWeight();
		}
		

		if (weightSum >0.0)
		{
			for(Particle p : particles)
			{
				
				double normalizedWeight = p.getWeight() / weightSum;
				p.setWeight(normalizedWeight);
			}
			
		}
		
	}
	
	/************** get the normalized  weight****************/
	public ArrayList<Double> getNormalizedWeights()
    {
		ArrayList<Double> normalizedWeights = new ArrayList<Double>();
		/*Calculate sum weight*/
		double weightSum = 0.0;
		for(Particle p:particles)
		{
			weightSum +=p.getWeight();
		}
		
		if (weightSum >0.0)
		{
			for(Particle p : particles)
			{
				double normalizedWeight = p.getWeight() / weightSum;
				normalizedWeights.add(normalizedWeight);
			}
		}
        return normalizedWeights;
    }
	
	/**************  estimate the location of the target*************************/
	/*** only particles with top ratio weight are used for estimation ********/
	public TwoDPoint estimateLocation( double ratio)
	{
		double weightSumX=0.0;
		double weightSumY=0.0;
		double sumWeight = 0.0;
		
		double sumX=0.0;
		double sumY=0.0;
		
		Collections.sort(particles);
		for(int i =0 ; i< Math.ceil(ratio*particles.size()); i++)
		{
			weightSumX+=particles.get(i).getX()*particles.get(i).getWeight();
			weightSumY+=particles.get(i).getY()*particles.get(i).getWeight();
			sumWeight+=particles.get(i).getWeight();
			
			
			sumX+=particles.get(i).getX();
			sumY+=particles.get(i).getY();
		}
		
		
		TwoDPoint p=null;
		if(sumWeight!=0)
		{
			p= new TwoDPoint (weightSumX/sumWeight,  weightSumY/sumWeight);
		}
		else
		{
			p= new TwoDPoint (sumX/particles.size(),  sumY/particles.size());
		}
		return p;
	}
	
	public ArrayList<Particle> getParticles()
	{
		return particles;
	}
	
	public double getAreaWidth() 
	{
		return areaWidth;
	}


	public void setAreaWidth(double areaWidth) 
	{
		this.areaWidth = areaWidth;
	}



	public double getAreaHeight() 
	{
		return areaHeight;
	}



	public void setAreaHeight(double areaHeight) 
	{
		this.areaHeight = areaHeight;
	}


	public void setObserver( IObservationModel observer)
	{
		this.observer =observer;
	}
	
	public IObservationModel getObserver()
	{
		return this.observer;
	}
	
	
	public void setPredictor (IMotionModel predictor)
	{
		this.predictor = predictor;
	}
	
	public IMotionModel getPredictor()
	{
		return this.predictor;
	}
	
	
	/**
	 * the width of the size
	 */
	private double areaWidth;
	
	/**
	 * the height of the site
	 */
	private double areaHeight;
	
	/**
	 * the set of particles
	 */
	private ArrayList<Particle> particles;
	
	/**
	 * the observation model
	 */
	private IObservationModel observer;
	
	/**
	 * the motion model
	 */
	private IMotionModel predictor;
}

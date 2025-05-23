package br.remoto.model.Proprioceptors;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.Type;

import br.remoto.model.Configuration;
import br.remoto.model.ReMoto;
import br.remoto.model.Musculotendon.Muscle.IntrafusalMuscle.Bag1.Models.Bag1_Mileusnic;
import br.remoto.model.Musculotendon.Muscle.IntrafusalMuscle.Bag2.Models.Bag2_Mileusnic;
import br.remoto.model.Musculotendon.Muscle.IntrafusalMuscle.Chain.Models.Chain_Mileusnic;
import br.remoto.model.Musculotendon.Muscle.IntrafusalMuscle.Lumped.Models.Lumped_Prochazka;
import br.remoto.model.Musculotendon.Tendon.Innerveted.Lumped.Models.Lumped_Crago;
import br.remoto.model.Neuron.Neuron;
import br.remoto.model.Neuron.SensoryFiber;
import br.remoto.servlet.SpindleSimulation;
import br.remoto.spindle_simulator.Input;
import br.remoto.util.Conversion;
import br.remoto.util.Sample;
import br.remoto.util.Signal;



public class Vestibular {
	
	private String cdNucleus;
	
	protected Sample samplerIbFiringRate;
	
	protected ArrayList IbFiringRateStore = new ArrayList();
	
	protected Lumped_Crago lumpedGTO;
	
	private double IbFiringRate;
	private double afferentNumber = 0;
	
	//private Neuron[][] neurons;
	
	public Vestibular(String cdNucleus, Neuron neurons[][], Configuration conf, String gto_model){
		
		this.cdNucleus = cdNucleus;
		
		samplerIbFiringRate = new Sample(conf.getDecimationFrequency(), 1000 / conf.getStep());
		
		if(gto_model.equals(ReMoto.lumpedGtoModelCrago))
			this.lumpedGTO = new Lumped_Crago(cdNucleus, conf);
		
		
		for(int x = ReMoto.indDT + 1; x < neurons.length; x++)
		{
			if( neurons[x] == null )
				continue;
			
			for(int y = 0; y < neurons[x].length; y++)
			{
				Neuron neu = neurons[x][y];
				
				if( neu.isActive() == false )
					continue;
				
				if( !neu.getCdNucleus().endsWith( cdNucleus ))
				{
					continue;
				}
				
				if( neu.getCategory().equals( ReMoto.AF ) )
				{
					((SensoryFiber)neu).setVestibular( this );	
				}
				
				if( neu.getType().equals(ReMoto.Ib)){
					afferentNumber = afferentNumber + 1;
				}
			}
		}
		
	}
	 
	public double calculateIbFiringRate(double force, double t){
		
		return lumpedGTO.calculateIbFiringRate(force, t);
		
	}
	
	public void atualize(double t, double force){
		
		//System.out.println("Number of Ib AFs: " + afferentNumber);
		
		IbFiringRate = calculateIbFiringRate(force, t) / afferentNumber;
		
		//IbFiringRateStore.add( new Signal( ReMoto.IbFiringRate, IbFiringRate, t) );
		samplerIbFiringRate.sample(IbFiringRateStore, "", t, IbFiringRate);
		
		//System.out.println("Ib: " + calculateIbFiringRate(force, t));
	}

	public ArrayList getIbFiringRateStore() {
		return IbFiringRateStore;
	}

	public void setIbFiringRateStore(ArrayList ibFiringRateStore) {
		IbFiringRateStore = ibFiringRateStore;
	}

	public String getCdNucleus() {
		return cdNucleus;
	}

	public void setCdNucleus(String cdNucleus) {
		this.cdNucleus = cdNucleus;
	}

	public double getIbFiringRate() {
		return IbFiringRate;
	}

	public void setIbFiringRate(double ibFiringRate) {
		IbFiringRate = ibFiringRate;
	}
	
	

}
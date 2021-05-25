package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	PowerOutageDAO podao;
	
	public Model() {
		podao = new PowerOutageDAO();
	}
	
	public List<Nerc> getNercList() {
		return podao.getNercList();
	}
	List<PowerOutage> outages=new ArrayList<>();
	List<PowerOutage> soluzionemassima=new ArrayList<>();
	long maxore=0;
	int maxanni;
	int ncoinvolti=0;
	public String worstCaseScenario(Nerc nerc,long maxore, int anni)
	{
		List<PowerOutage> complete= new ArrayList<PowerOutage>(podao.getPowerOutages());
		outages= new ArrayList<PowerOutage>();
		for(PowerOutage p: complete)
		{
			if(p.nercid==nerc.getId())
			{
				outages.add(p);
			}
		}
		this.maxore=maxore*60;
		maxanni=anni;
		ncoinvolti=0;
		calcola2(0,new ArrayList<PowerOutage>());
		String s=Integer.toString(ncoinvolti)+"\n";
		
		for(PowerOutage p: soluzionemassima)
		{
			s+=p.id+" "+ p.customersn+" "+p.begin+" "+p.end+"\n";
		}
		return s;
	}
	
	
	
	public void calcola(int livello, List<PowerOutage>parziale)
	{
		//caso terminale
		if(sommaore(parziale)>maxore)
		{
			return;
		}
		if(livello==outages.size())
		{
			if(utenticoinvolti(parziale)>ncoinvolti)
			{
				ncoinvolti=utenticoinvolti(parziale);
				soluzionemassima=new ArrayList<PowerOutage>(parziale);
			}
			return;
		}
		if(sommaore(parziale)<maxore)
		{
			
			if(utenticoinvolti(parziale)>ncoinvolti)
			{
				ncoinvolti=utenticoinvolti(parziale);
				soluzionemassima=new ArrayList<PowerOutage>(parziale);
			}
		}
		
		for(PowerOutage p:outages)
		{
			if(!parziale.contains(p))
			{
				parziale.add(p);
				if(isValid(parziale))
					calcola(livello+1,parziale);
				parziale.remove(p);
			}
		}
	}
	
	public void calcola2(int livello, List<PowerOutage>parziale)
	{
		//caso terminale
		long sommaore=sommaore(parziale);
		int coinvolti=utenticoinvolti(parziale);
		if(sommaore>maxore)
		{
			return;
		}
		
		if(coinvolti>ncoinvolti)
		{
			ncoinvolti=coinvolti;
			soluzionemassima=new ArrayList<PowerOutage>(parziale);
		}
		
		if(livello==outages.size())
			return;
		else
		{
			PowerOutage nuovoPo = outages.get(livello);
			if(parziale.size()==0||Math.abs((nuovoPo.begin.getYear())-(parziale.get(0).begin.getYear()))<maxanni) 
			{
				parziale.add(nuovoPo);
				calcola(livello+1,parziale);
				parziale.remove(nuovoPo);
				calcola(livello+1,parziale);
			}else {
				return;
			}
		
		}
		
		
		
		
		
	}
	public long sommaore(List<PowerOutage> in)
	{
		long somma=0;
		for(PowerOutage p:in)
		{
			long diff = p.begin.until(p.end,ChronoUnit.MINUTES);	
			//long diff=ChronoUnit.HOURS.between(p.begin,p.end);
			somma+=diff;
			//long df=p.end.toEpochSecond(ZoneOffset.UTC);
			//somma+=(ChronoUnit.hours( //p.end.-p.begin)
			
		}
		return somma;
	}
	
	public int utenticoinvolti(List<PowerOutage> in)
	{
		int somma=0;
		for(PowerOutage p:in)
		{
			somma+=p.customersn;
		}
		return somma;
	}
	
	public boolean isValid(List<PowerOutage> in)
	{
		for(int i=0;i<in.size()-1;i++)
		{
			if(Math.abs((in.get(in.size()-1).begin.getYear())-(in.get(i).begin.getYear()))>maxanni)
			{
				return false;
			}
		}
		return true;
	}
	public boolean isValid2(List<PowerOutage> in)
	{
			if(in.size()==1||Math.abs((in.get(in.size()-1).begin.getYear())-(in.get(0).begin.getYear()))<maxanni)
			{
				return true;
			}
		return false;
	}
}

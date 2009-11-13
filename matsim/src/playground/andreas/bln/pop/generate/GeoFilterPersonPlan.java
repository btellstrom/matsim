package playground.andreas.bln.pop.generate;

import org.matsim.api.basic.v01.population.PlanElement;
import org.matsim.api.core.v01.ScenarioImpl;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.MatsimPopulationReader;
import org.matsim.core.population.PopulationImpl;
import org.matsim.core.population.PopulationReader;
import org.matsim.core.population.routes.NetworkRouteWRefs;

import playground.andreas.bln.pop.NewPopulation;


/**
 * Filters persons with plans which do not have one node of a given network in common.
 * Initial plans without link and route information (origPlansFile)
 * should be run using the whole network (bigNetworkFile).
 * The resulting plansfile (inPlansFile) of iteration zero will be used to determine,
 * whether an agent's plan "touches" a given area of the whole area or not 
 * (targetNetworkFile is an extract of bigNetworkFile). "Touch" means at least on node
 * of the agent's plan is part of the targetNetworkFile. In case it touches, the 
 * agent's original plan from origPlansFile, will be dumped to outPlansFile.
 * outPlansFile can be used fit the plan to an new network, e.g. targetNetworkFile. 
 * 
 * @author aneumann
 *
 */
public class GeoFilterPersonPlan extends NewPopulation {

	private int planswritten = 0;
	private int personshandled = 0;
	private NetworkLayer targetNet;
	private PopulationImpl origPop;


	public GeoFilterPersonPlan(PopulationImpl plans, String filename, PopulationImpl origPop, NetworkLayer targetNet) {
		super(plans, filename);
		this.targetNet = targetNet;
		this.origPop = origPop;
	}

	
	@Override
	public void run(Person person) {
		
		this.personshandled++;
		
		if(person.getPlans().size() != 1){
			System.err.println("Person got more than one plan");
		} else {
			
			Plan plan = person.getPlans().get(0);
			boolean keepPlan = false;

			for (PlanElement planElement : plan.getPlanElements()) {
				if(planElement instanceof LegImpl){
					for (Node node : ((NetworkRouteWRefs)((LegImpl) planElement).getRoute()).getNodes()) {
						if(this.targetNet.getNodes().containsValue(node)){
							keepPlan = true;
							break;
						}
					}
				}
				if(keepPlan){
					break;
				}
			}
			
			if(keepPlan){
				this.popWriter.writePerson(this.origPop.getPersons().get(person.getId()));
				this.planswritten++;
			}
			
			
		}

	}
	
	public static void main(final String[] args) {
		Gbl.startMeasurement();
		
		ScenarioImpl sc = new ScenarioImpl();

		String bigNetworkFile = "./bb_cl.xml.gz";
		String targetNetworkFile = "./hundekopf_cl.xml.gz";
		String origPlansFile = "./car_only.xml.gz";
		String inPlansFile = "./0.plans.xml.gz";
		String outPlansFile = "./plan_hundekopf2.xml.gz";
		
		NetworkLayer bigNet = new NetworkLayer();
		new MatsimNetworkReader(bigNet).readFile(bigNetworkFile);

		NetworkLayer targetNet = new NetworkLayer();
		new MatsimNetworkReader(targetNet).readFile(targetNetworkFile);

		PopulationImpl inPop = new PopulationImpl();
		PopulationReader popReader = new MatsimPopulationReader(inPop, bigNet);
		popReader.readFile(inPlansFile);
		
		PopulationImpl origPop = new PopulationImpl();
		PopulationReader origPopReader = new MatsimPopulationReader(origPop, bigNet);
		origPopReader.readFile(origPlansFile);

		GeoFilterPersonPlan dp = new GeoFilterPersonPlan(inPop, outPlansFile, origPop, targetNet);
		dp.run(inPop);
		
		System.out.println(dp.personshandled + " persons handled; " + dp.planswritten + " plans written to file");
		dp.writeEndPlans();

		Gbl.printElapsedTime();
	}
}

package org.moonlightcontroller.controller;

import org.moonlightcontroller.aggregator.ApplicationAggregator;
import org.moonlightcontroller.aggregator.IApplicationAggregator;
import org.moonlightcontroller.bal.BoxApplication;
import org.moonlightcontroller.events.EventManager;
import org.moonlightcontroller.events.IEventManager;
import org.moonlightcontroller.registry.IApplicationRegistry;
import org.moonlightcontroller.registry.RegisteredBoxApplication;
import org.moonlightcontroller.southbound.server.ISouthboundServer;
import org.moonlightcontroller.southbound.server.SouthboundServer;
import org.moonlightcontroller.topology.ITopologyManager;
import org.openbox.dashboard.NetworkInformationService;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * The class representing the Moonlight Controller 
 *
 */
public class MoonlightController {

	private final ITopologyManager topology;
	private ISouthboundServer sserver;

	/**
	 * Initializes a new controller with the given parameters
	 * @param topology The network topology
	 * @param port The port on which the controller should be listening
	 */
	public MoonlightController(
			ITopologyManager topology,
			int port) {
		this.topology = topology;
		this.sserver = new SouthboundServer(port);
	}
	
	/**
	 * Starts the controller
	 * Iterates over all loaded OpenBox applications and sends them for aggregation
	 * Also, registers them for event handling
     * @param applicationRegistry An already loaded application registry
	 */
	public void start(IApplicationRegistry applicationRegistry){
        NetworkInformationService.getInstance().setTopology(topology);

		updateApps(applicationRegistry);

		try {
			this.sserver.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Updates the applications from the registry and performs aggregation
     * @param applicationRegistry An already loaded application registry
     */
    public void updateApps(IApplicationRegistry applicationRegistry) {
        List<BoxApplication> apps = applicationRegistry.getApplications().stream().map(RegisteredBoxApplication::getApplication).collect(toList());

        IApplicationAggregator aggregator = ApplicationAggregator.getInstance();
        aggregator.performAggregation(apps);

        IEventManager eManager = EventManager.getInstance();
        eManager.addApplications(apps);

        NetworkInformationService.getInstance().onPostAggregation(applicationRegistry, aggregator);

        for (BoxApplication app : apps) {
            eManager.HandleAppStart(app.getName());
        }
	}

}
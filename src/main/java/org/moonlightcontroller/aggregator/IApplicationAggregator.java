package org.moonlightcontroller.aggregator;

import java.util.List;

import org.moonlightcontroller.bal.BoxApplication;
import org.openboxprotocol.protocol.IStatement;
import org.openboxprotocol.protocol.topology.ILocationSpecifier;

public interface IApplicationAggregator {
	void addApplications(List<BoxApplication> apps);
	void addApplication(BoxApplication apps);
	void performAggregation();
	List<IStatement> getStatements(ILocationSpecifier loc);
}
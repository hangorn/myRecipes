package es.magDevs.myRecipes.dal;

public class Constants {

	public static final boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
}

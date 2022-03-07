package fr.index.cloud.ens.customizer.plugin.statistics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CloudEnsUserStatistics {

    /**
     * Consulted documents paths.
     */
    private final Set<String> paths;


    /**
     * Constructor.
     */
    public CloudEnsUserStatistics() {
        super();
        this.paths = Collections.synchronizedSet(new HashSet<>());
    }


    public Set<String> getPaths() {
        return paths;
    }

}

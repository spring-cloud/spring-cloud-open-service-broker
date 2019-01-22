package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata for service plans.
 * Holds keys specified in cloudfoundry profile in OSB specs,
 * as well as custom non-standard keys.
 */
public class Metadata {

    private List<Cost> costs = new ArrayList<Cost>();

    private List<String> bullets = new ArrayList<>();

    private String displayName;

    /**
     * Custom non standard metadata need to be specified under extra hash
     */
    private final Map<String, Object> extra = new HashMap<>();


    public List<Cost> getCosts() { return costs; }
    public void setCosts(List<Cost> costs) { this.costs = costs; }

    public List<String> getBullets() { return bullets; }

    public void setBullets(List<String> bullets) { this.bullets = bullets; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Map<String, Object> getExtra() { return extra; }

    public void setExtra(Map<String, Object> extra) {
        this.extra.putAll(extra);
    }


    public Map<String, Object> toModel() {
        HashMap<String, Object> model = new HashMap<>();
        if (costs != null) {
            model.put("costs", costs);
        }
        if (bullets != null) {
            model.put("bullets", bullets);
        }
        if (displayName != null) {
            model.put("displayName", displayName);
        }
        model.putAll(this.extra);
        return model;
    }

    public static class Cost {

        private Map<String, Double> amount = new HashMap<>();

        private String unit;

        public Map<String, Double> getAmount() {
            return amount;
        }

        public void setAmount(Map<String, Double> amount) {
            this.amount = amount;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}

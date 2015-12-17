package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@pivotal.io
 *
 * @deprecated This class is no longer used internally to represent service instances. Implementing brokers should
 * create their own class to represent service instances and persist them as necessary. The will remain in the project
 * for a time as a convenience, but it should no longer be used by implementing brokers.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstance {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	private String serviceInstanceId;
	
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	
	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;
	
	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	@JsonSerialize
	@JsonProperty("last_operation")
	private GetLastServiceOperationResponse lastOperation;
	
	@JsonIgnore
	private boolean async;
	
	@SuppressWarnings("unused")
	private ServiceInstance() {}
	
	/**
	 * Create a ServiceInstance from a create request. If fields 
	 * are not present in the request they will remain null in the  
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(CreateServiceInstanceRequest request) {
		this.serviceDefinitionId = request.getServiceDefinitionId();
		this.planId = request.getPlanId();
		this.organizationGuid = request.getOrganizationGuid();
		this.spaceGuid = request.getSpaceGuid();
		this.serviceInstanceId = request.getServiceInstanceId();
		this.lastOperation = new GetLastServiceOperationResponse(OperationState.IN_PROGRESS, "Provisioning", false);
	}
	
	/**
	 * Create a ServiceInstance from a delete request. If fields 
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(DeleteServiceInstanceRequest request) {
		this.serviceInstanceId = request.getServiceInstanceId();
		this.planId = request.getPlanId();
		this.serviceDefinitionId = request.getServiceDefinitionId();
		this.lastOperation = new GetLastServiceOperationResponse(OperationState.IN_PROGRESS, "Deprovisioning", false);
	}
	
	/**
	 * Create a service instance from an update request. If fields
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(UpdateServiceInstanceRequest request) {
		this.serviceInstanceId = request.getServiceInstanceId();
		this.planId = request.getPlanId();
		this.lastOperation = new GetLastServiceOperationResponse(OperationState.IN_PROGRESS, "Updating", false);
	}
	
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public boolean isAsync() {
		return async;
	}

	public ServiceInstance and() {
		return this;
	}

	public ServiceInstance withLastOperation(GetLastServiceOperationResponse lastOperation) {
		this.lastOperation = lastOperation;
		return this;
	}

	public ServiceInstance withDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
		return this;
	}

	public ServiceInstance withAsync(boolean async) {
		this.async = async;
		return this;
	}

	public GetLastServiceOperationResponse getServiceInstanceLastOperation() {
		return lastOperation;
	}
	
}

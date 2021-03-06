package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafMedicationStatement;
import org.hl7.davinci.atr.server.service.MedicationStatementService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class MedicationStatementResourceProvider extends AbstractJaxRsResourceProvider<MedicationStatement> {

	public static final String RESOURCE_TYPE = "MedicationStatement";
    public static final String VERSION_ID = "1";
    @Autowired
    MedicationStatementService service;
    
    public MedicationStatementResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<MedicationStatement> getResourceType() {
		return MedicationStatement.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationStatement/1/_history/3.0
	 * @param theId : Id of the MedicationStatement
	 * @return : Object of MedicationStatement information
	 */
	@Read(version=true)
    public MedicationStatement readOrVread(@IdParam IdType theId) {
		int id;
		MedicationStatement medicationStatement;
		try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
		   // this is a vread  
			medicationStatement = service.getMedicationStatementByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			medicationStatement = service.getMedicationStatementById(id);
		}
		return medicationStatement;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCategory
	 * @param theSource
	 * @param thePartOf
	 * @param theMedication
	 * @param theContext
	 * @param thePatient
	 * @param theSubject
	 * @param theCode
	 * @param theEffective
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
	    javax.servlet.http.HttpServletRequest theServletRequest,
	
	    @Description(shortDefinition = "The resource identity")
	    @OptionalParam(name = MedicationStatement.SP_RES_ID)
	    TokenAndListParam theId,
	
	    @Description(shortDefinition = "A MedicationStatement identifier")
	    @OptionalParam(name = MedicationStatement.SP_IDENTIFIER)
	    TokenAndListParam theIdentifier,
	    
	    @Description(shortDefinition = "Who or where the information in the statement came from")
	    @OptionalParam(name = MedicationStatement.SP_STATUS)
	    TokenAndListParam theStatus,
	    
	    @Description(shortDefinition = "Returns statements of this category of medicationstatement")
	    @OptionalParam(name = MedicationStatement.SP_CATEGORY)
	    TokenAndListParam theCategory,
	    
	    @Description(shortDefinition = "Who or where the information in the statement came from")
	    @OptionalParam(name = MedicationStatement.SP_SOURCE)
	    ReferenceAndListParam theSource,
	    
	    @Description(shortDefinition = "Returns statements that are part of another event.")
	    @OptionalParam(name = MedicationStatement.SP_PART_OF)
	    ReferenceAndListParam thePartOf,
	    
	    @Description(shortDefinition = "Return statements of this medication reference")
	    @OptionalParam(name = MedicationStatement.SP_MEDICATION)
	    ReferenceAndListParam theMedication,
	    
	    @Description(shortDefinition = "Returns statements for a specific context (episode or episode of Care).")
	    @OptionalParam(name = MedicationStatement.SP_CONTEXT)
	    ReferenceAndListParam theContext,
	    
	    @Description(shortDefinition = "Returns statements for a specific patient.")
	    @OptionalParam(name = MedicationStatement.SP_PATIENT)
	    ReferenceAndListParam thePatient,
	    
	    @Description(shortDefinition = "The identity of a patient, animal or group to list statements for")
	    @OptionalParam(name = MedicationStatement.SP_SUBJECT)
	    ReferenceAndListParam theSubject,
	    
	    @Description(shortDefinition = "Return statements of this medication code")
	    @OptionalParam(name = MedicationStatement.SP_CODE)
	    TokenAndListParam theCode,
	    
	    @Description(shortDefinition = "Date when patient was taking (or not taking) the medication")
	    @OptionalParam(name = MedicationStatement.SP_EFFECTIVE)
	    DateRangeParam theEffective,
	
	    @IncludeParam(allow = {"*"})
	    Set<Include> theIncludes,
	
	    @Sort
	    SortSpec theSort,
	
	    @Count
	    Integer theCount) {
	
	        SearchParameterMap paramMap = new SearchParameterMap();
	        paramMap.add(MedicationStatement.SP_RES_ID, theId);
	        paramMap.add(MedicationStatement.SP_IDENTIFIER, theIdentifier);
	        paramMap.add(MedicationStatement.SP_STATUS, theStatus);
	        paramMap.add(MedicationStatement.SP_CONTEXT, theContext);
	        paramMap.add(MedicationStatement.SP_PATIENT, thePatient);
	        paramMap.add(MedicationStatement.SP_MEDICATION, theMedication);
	        paramMap.add(MedicationStatement.SP_CODE, theCode);
	        paramMap.add(MedicationStatement.SP_EFFECTIVE, theEffective);
	        paramMap.add(MedicationStatement.SP_SUBJECT, theSubject);
	        paramMap.add(MedicationStatement.SP_PART_OF, thePartOf);
	        paramMap.add(MedicationStatement.SP_CATEGORY, theCategory);
	        paramMap.add(MedicationStatement.SP_SOURCE, theSource);
	
	        paramMap.setIncludes(theIncludes);
	        paramMap.setSort(theSort);
	        paramMap.setCount(theCount);
	        
	        final List<MedicationStatement> results = service.search(paramMap);
	
	        return new IBundleProvider() {
	            final InstantDt published = InstantDt.withCurrentTime();
	            @Override
	            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                List<IBaseResource> medicationStatementList = new ArrayList<IBaseResource>();
	                for(MedicationStatement theMedicationStatement : results){
	                	medicationStatementList.add(theMedicationStatement);
	                }
	                return medicationStatementList;
	            }
	
	            @Override
	            public Integer size() {
	                return results.size();
	            }
	
	            @Override
	            public InstantDt getPublished() {
	                return published;
	            }
	
	            @Override
	            public Integer preferredPageSize() {
	                return null;
	            }
	
				@Override
				public String getUuid() {
					return null;
				}
	        };
    }
	
    /**	
     * The create  operation saves a new resource to the server, 
     * allowing the server to give that resource an ID and version ID.
     * Create methods must be annotated with the @Create annotation, 
     * and have a single parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Create methods must return an object of type MethodOutcome . 
     * This object contains the identity of the created resource.
     * Example URL to invoke this method (this would be invoked using an HTTP POST, 
     * with the resource in the POST body): http://<server name>/<context>/fhir/MedicationStatement
     * @param theMedicationStatement
     * @return
     */
    @Create
    public MethodOutcome createMedicationStatement(@ResourceParam MedicationStatement theMedicationStatement) {
         
    	// Save this MedicationStatement to the database...
    	DafMedicationStatement dafMedicationStatement = service.createMedicationStatement(theMedicationStatement);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationStatement.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/MedicationStatement/1
     * @param theId
     * @param theMedicationStatement
     * @return
     */
    @Update
    public MethodOutcome updateMedicationStatementById(@IdParam IdType theId, 
    										@ResourceParam MedicationStatement theMedicationStatement) {
    	int id;
    	try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
    	
    	Meta meta = new Meta();
		meta.setVersionId("1");
		Date date = new Date();
		meta.setLastUpdated(date);
		theMedicationStatement.setMeta(meta);
    	// Update this MedicationStatement to the database...
    	DafMedicationStatement dafMedicationStatement = service.updateMedicationStatementById(id, theMedicationStatement);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationStatement.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<MedicationStatement> getMedicationStatementForBulkDataRequest(List<String> patients, Date start, Date end) {
 
		List<MedicationStatement> medStatementList = service.getMedicationStatementForBulkData(patients, start, end);
		return medStatementList;
	}

}

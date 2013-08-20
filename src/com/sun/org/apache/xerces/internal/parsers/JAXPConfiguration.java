package com.sun.org.apache.xerces.internal.parsers;

import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import com.sun.org.apache.xerces.internal.jaxp.JAXPValidatorComponent;
import com.sun.org.apache.xerces.internal.jaxp.validation.InsulatedValidatorComponent;
import com.sun.org.apache.xerces.internal.jaxp.validation.XercesSchema;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
/**
 * {@link com.sun.org.apache.xerces.internal.xni.parser.XMLParseException} that
 * includes a JAXP {@link ValidatorHandler} in the middle. 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 *     Venu Gopal (k.venugopal@sun.com)
 */
public class JAXPConfiguration extends XIncludeParserConfiguration {
    
    /** can be null. */
    private final Schema fSchema;
    
    /**
     * 
     * @param grammar
     *      when non-null, the parser will include validation /
     *      infoset augmentation by this {@link Schema}.
     */
    public JAXPConfiguration(Schema schema){
        this.fSchema = schema;
    }
    
    protected void configurePipeline() {
        super.configurePipeline();
        
        if (fSchema != null) {
            if( isXNICapabaleSchema(fSchema) ) {
                // if the validator is also from this Xerces,
                // we will use the XNI-based validator for
                // better performance
                InsulatedValidatorComponent v = ((XercesSchema)fSchema).newXNIValidator();
                addComponent(v);
                
                fLastComponent.setDocumentHandler(v.getValidator());
                v.getValidator().setDocumentSource(fLastComponent);
                fLastComponent = v.getValidator();
                v.getValidator().setDocumentHandler(fDocumentHandler);
            } else {
                // otherwise wrap that into JAXPValidatorComponent.
                XMLDocumentFilter validator = null;
                ValidatorHandler validatorHandler = fSchema.newValidatorHandler();
            
                validator = new JAXPValidatorComponent(validatorHandler);
                addComponent((XMLComponent)validator);
                
                fLastComponent.setDocumentHandler(validator);
                validator.setDocumentSource(fLastComponent);
                fLastComponent = validator;
                validator.setDocumentHandler(fDocumentHandler);
            }
        }
    }
    
    /**
     * Checks if the given {@link Schema} speaks XNI. 
     */
    private static boolean isXNICapabaleSchema( Schema s ) {
        if(!(s instanceof XercesSchema ))   return false;
        
        try {
            String v = System.getProperty(JAXPConfiguration.class.getName()+".noSchemaOptimization");
            if(v==null)
                // there might be a bug in the optimization we do.
                // this property provides an escape hatch for such a situation
                // by forcing non-optimized way.
                return false;
        } catch( Throwable t ) {
            ;
        }
        
        // otherwise if schema derives from XercesSchema,
        // we set up better optimized pipeline.
        return true;
    }
    
    public boolean getFeatureDefaultValue(String featureId){

        // reset every component
        int count = fComponents.size();
        for (int i = 0; i < count; i++) {
            XMLComponent c = (XMLComponent) fComponents.get(i);
            Boolean bo = c.getFeatureDefault(featureId);
            if(bo != null){
                return bo.booleanValue();
            }
            //null if component doesn't recognize this feature.
            //continue it might be present in some other components.
            //it might make sense to store default values of feature for 
            //the current configuration that would make the lookup faster.
            
        }
        //if it wasn't found in all the components return false;
        return false;
    }
    
}


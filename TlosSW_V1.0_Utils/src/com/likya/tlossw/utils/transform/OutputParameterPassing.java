package com.likya.tlossw.utils.transform;

/**
 [Class description.  This class is being used for parameter pass between jobs.]

 [Other notes, including guaranteed invariants, usage instructions and/or examples, reminders
 about desired improvements, etc.]

 @author <A HREF="mailto:hakan.saribiyik@likyateknoloji.com">Hakan Saribiyik</A>
 @version $Revision: 1.1.1.1 $ $Date: 2012/08/17 15:15:25 $
 @see [String]
 @see [URL]
 @see [ParameterPassing#passParameter]
 **/

import java.math.BigInteger;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.OutParamDocument.OutParam;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class OutputParameterPassing {

	public static boolean putOutputParameter(JobProperties jobProperties, Object paramRef, String parameterName) {

		boolean paramF = false;

		if (!(jobProperties instanceof JobProperties)) {
			return false;
		}
		
		SpecialParameters specialParameter = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();
		if (specialParameter == null) {
			jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().addNewSpecialParameters();
			specialParameter = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();
		}
		if (specialParameter.getOutParam() == null) {
			specialParameter.addNewOutParam();
		}
		OutParam outParam = specialParameter.getOutParam();

		for (int j = 0; j < outParam.sizeOfParameterArray(); j++) {
			paramF = outParam.getParameterArray(j).getName().equalsIgnoreCase(parameterName);
			if (paramF) {
				outParam.getParameterArray(j).getPreValue().setStringValue(paramRef.toString());
				outParam.getParameterArray(j).getPreValue().setType(new BigInteger("2"));
				outParam.getParameterArray(j).setValueString(paramRef.toString());
				break;
			}
		}

		// Tanimli output degiskenlerden birisi degilse ekleyelim ..
		if (!paramF) {
			QName qName = Parameter.type.getOuterType().getDocumentElementName();
			XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

			ParameterDocument newParameter = ParameterDocument.Factory.newInstance(xmlOptions);
			Parameter parameter = newParameter.addNewParameter();

			parameter.setName(parameterName);
			parameter.addNewPreValue();
			parameter.getPreValue().setStringValue(paramRef.toString());
			parameter.getPreValue().setType(new BigInteger("2"));
			parameter.setValueString(paramRef.toString());
			parameter.xmlText(xmlOptions);
			String parameterXML = parameter.xmlText(xmlOptions);
			try {
				Parameter newParameter2 = ParameterDocument.Factory.parse(parameterXML).getParameter();
				outParam.insertNewParameter(0);
				outParam.setParameterArray(0, newParameter2);
				System.out.println("Output parametre = " + parameterName);
				System.out.println("Degeri : " + paramRef.toString());
			} catch (XmlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return true;
		
	}

}

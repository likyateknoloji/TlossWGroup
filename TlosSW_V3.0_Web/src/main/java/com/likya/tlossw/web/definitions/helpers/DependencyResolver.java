package com.likya.tlossw.web.definitions.helpers;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.collections.iterators.ArrayIterator;

import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.exceptions.TransformCodeCreateException;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.SessionMediator;

public class DependencyResolver extends TlosSWBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5541555688176091580L;

	// public DependencyResolver() {
	//
	// }

	public static HashMap<String, Parameter> handleDependency(JobProperties scheduledJob, DependencyList dependentJobList, SessionMediator sessionMediator) throws UnresolvedDependencyException, TransformCodeCreateException {

		JobProperties jobProperties = scheduledJob;

		if (dependentJobList != null) {

			String dependencyExpression = dependentJobList.getDependencyExpression().trim().toUpperCase();
			Item[] dependencyArray = jobProperties.getDependencyList().getItemArray();

			return isJobDependencyResolved(scheduledJob, dependencyExpression, dependencyArray, sessionMediator);
		}

		return null;
	}

	public static HashMap<String, Parameter> isJobDependencyResolved(JobProperties ownerJob, String dependencyExpression, Item[] dependencyArray, SessionMediator sessionMediator) throws UnresolvedDependencyException {

		String ownerJsName = ownerJob.getBaseJobInfos().getJsName();

		dependencyExpression = dependencyExpression.replace("AND", "&&");
		dependencyExpression = dependencyExpression.replace("OR", "||");

		ArrayIterator dependencyArrayIterator = new ArrayIterator(dependencyArray);

		HashMap<String, Parameter> depJobParameterlList = new HashMap<String, Parameter>();

		while (dependencyArrayIterator.hasNext()) {

			Item item = (Item) (dependencyArrayIterator.next());

			JobProperties jobPropertiesDep = sessionMediator.getDbOperations().getJobFromId("sjData", sessionMediator.getWebAppUser().getId(), 1, item.getJsId());

			if (jobPropertiesDep != null) {
				for (Parameter parameter : jobPropertiesDep.getLocalParameters().getOutParam().getParameterArray()) {
					if(parameter.getActive()) {
						
						parameter.setJsId(item.getJsId());
						
						if (!parameter.getIoName().isEmpty())
							depJobParameterlList.put(parameter.getIoName(), parameter);
						else
							depJobParameterlList.put(parameter.getId() + "", parameter);
						
					}
				}
				if (dependencyExpression.indexOf(item.getDependencyID().toUpperCase()) < 0) {
					String errorMessage = "     > " + ownerJsName + " isi icin hatali bagimlilik tanimlamasi yapilmis ! (" + dependencyExpression + ") kontrol ediniz.";

					throw new UnresolvedDependencyException(errorMessage);
				}

			} else {
				System.out.print("     > jobRuntimeProperties.getJobProperties() == null !!");
				throw new UnresolvedDependencyException("     > jobRuntimeProperties.getJobProperties() == null !!");
			}

		}

		return depJobParameterlList;
	}

}

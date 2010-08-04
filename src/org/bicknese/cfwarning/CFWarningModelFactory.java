package org.bicknese.cfwarning;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.model.AdaptableList;

public class CFWarningModelFactory {
	
	private static CFWarningModelFactory instance = new CFWarningModelFactory();
	private boolean registryLoaded = false;
	IFileParser parser = null;
	
	private CFWarningModelFactory() {
	}
	
	public static CFWarningModelFactory getInstance() {
		return instance;
	}

	public IProject getProject(String filePath) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(filePath.substring(0,filePath.indexOf("/",0)));
	}
	
	public IFile getFile(IProject project, String filePath) {
		return project.getFile(filePath.substring(filePath.indexOf("/")+1,filePath.length()));
	}
	
	public AdaptableList getWarnings(String filePath) {
		
		try {
			IProject project = getProject(filePath);
			IFile file =  getFile(project, filePath);
			String extension = file.getFileExtension();
			
			if(extension != null && extension.equals(CFWarningConstants.EXTENSION)) {
				return getWarnings(file);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
			//System.exit(1);
		}
		return null;
	}
	
	private AdaptableList getWarnings(IFile file) {
		
		Function[] topLevel = getToc(file);
		
		AdaptableList list = new AdaptableList();
		
		list.add(new GeneralInfo(topLevel));
		
		for (int i=0;i<topLevel.length;i++) {
			addWarnings(list,topLevel[i]);
		}
		
		return list;
	}

	private void addWarnings(AdaptableList list, Function function) {
		Object[] children = function.getChildren(function);
		
		if(children.length > 0) {
			list.add(function);
			
			for (int i = 0; i < children.length; ++i) {
				addWarnings(list, (Warning) children[i]);
			}
		}
	}
	
	private void addWarnings(AdaptableList list, Warning warning) {
		list.add(warning);
	}

	private Function[] getToc(IFile file) {
		if (registryLoaded == false) loadParser();
		
		return parser.parse(file);
	}

	// This is going through lists. I'm not sure we need to do this.
	private void loadParser() {
		
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		
		IExtensionPoint point = pluginRegistry.getExtensionPoint(CFWarningConstants.PLUGIN_ID, CFWarningConstants.PP_WARNING_PARSER);
		
		if(point != null) {
			
			IExtension[] extensions = point.getExtensions();
			
			for(int i=0; i < extensions.length; i++) {
				
				IExtension currentExtension = extensions[i];
				if(i == extensions.length - 1) {
					IConfigurationElement[] configElements = currentExtension.getConfigurationElements();
					
					for(int j=0; j < configElements.length; j++) {
						IConfigurationElement config = configElements[j];
						if(config.getName().equals(CFWarningConstants.TAG_PARSER)) {
							
							processParseElement(config);
							break;	
						}
					}
				}
			}
		}
		
		
		if(parser == null) {
			parser = new DefaultParser();
		}

		registryLoaded = true;
	}

	// tries to create an instance of the file parser.
	private void processParseElement(IConfigurationElement config) {
		try {
			parser = (IFileParser)config.createExecutableExtension(CFWarningConstants.ATT_CLASS);
		} catch (CoreException e) {
			System.out.println("Unable_to_create_file_parser"+e.getStatus().getMessage());
			parser = null;
		}
	}

}

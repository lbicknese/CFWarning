package org.bicknese.cfwarning;

import org.eclipse.core.resources.IFile;

public interface IFileParser {

	public Function[] parse(IFile file);

}

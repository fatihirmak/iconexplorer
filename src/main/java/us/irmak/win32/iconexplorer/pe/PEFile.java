package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

public class PEFile extends Struct {
	DOSHeader dosHeader;
	PEHeader peHeader;
	OptionalHeader optionalHeader;
	SectionHeader[] sectionHeaders;
	
	ResourceDirectory resourceDirectory;
	
	public PEFile() {
		super();
	}
	public PEFile(ByteBuffer stream) {
		super(stream);
		
		dosHeader = new DOSHeader(stream);
		stream.position(dosHeader.peHeaderOffset);
		peHeader = new PEHeader(stream);
		stream.position(stream.position() + peHeader.optionalHeaderSize);
		sectionHeaders = new SectionHeader[peHeader.numberOfSections];
		
		for (int i = 0; i < peHeader.numberOfSections; i++) {
			sectionHeaders[i] = new SectionHeader(stream);
			Object sectionData = sectionHeaders[i].getSection();
			if (sectionData instanceof ResourceDirectory) {
				resourceDirectory = (ResourceDirectory) sectionData;
			}
		}
	}
	
	public Optional<SectionHeader> getSectionHeader(String name) {
		return Arrays.asList(sectionHeaders).stream().filter(sh -> name.equals(sh.getName())).findFirst();
	}
	
	public ResourceDirectory getResourceDirectory() {
		return resourceDirectory;
	}
}

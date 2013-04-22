package lucene;

import java.net.URL;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class ExtractText {
	public String title;   	//title
	public String description;	//description 
	public String WebContent;
	public void HTMLtoText(String input) throws Exception {
		String sourceUrlString;
		sourceUrlString=input;
		if (sourceUrlString.indexOf(':')==-1) 
			sourceUrlString="file:///"+sourceUrlString;
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		//System.out.println("Document title:");
		title=getTitle(source);
		//System.out.println(title==null ? "(none)" : title);

		//System.out.println("\nDocument description:");
		description=getMetaValue(source,"description");
		//System.out.println(description==null ? "(none)" : description);

		//System.out.println("\nDocument keywords:");
		String keywords=getMetaValue(source,"keywords");
		//System.out.println(keywords==null ? "(none)" : keywords);

		/*
		System.out.println("\nLinks to other documents:");
		List<Element> linkElements=source.getAllElements(HTMLElementName.A);
		for (Element linkElement : linkElements) {
			String href=linkElement.getAttributeValue("href");
			if (href==null) continue;
			// A element can contain other tags so need to extract the text from it:
			String label=linkElement.getContent().getTextExtractor().toString();
			System.out.println(label+" <"+href+'>');
		}
		*/

		//System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");
		//System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString()+"\n");
		WebContent = source.getTextExtractor().setIncludeAttributes(true).toString();
		/*
		System.out.println("\nSame again but this time extend the TextExtractor class to also exclude text from P elements and any elements with class=\"control\":\n");
		TextExtractor textExtractor=new TextExtractor(source) {
			public boolean excludeElement(StartTag startTag) {
				return startTag.getName()==HTMLElementName.P || "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
			}
		};
		System.out.println(textExtractor.setIncludeAttributes(true).toString());
		*/
  }

	public static String getTitle(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}

	public static String getMetaValue(Source source, String key) {
		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,"name",key,false);
			if (startTag==null) return null;
			if (startTag.getName()==HTMLElementName.META)
				return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
			pos=startTag.getEnd();
		}
		return null;
	}
}

package superstartrek.client.utils.htmlresource;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;

import superstartrek.gwtcompiler.HtmlResourceGenerator;

@DefaultExtensions(value = {".html"})
@ResourceGeneratorType(HtmlResourceGenerator.class)
public interface HtmlResource extends ResourcePrototype {
  String getText();
}

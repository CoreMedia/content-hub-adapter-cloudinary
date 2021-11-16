import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ContentHubCloudinary_properties from "./ContentHubCloudinary_properties";

interface ContentHubStudioCloudinaryPluginConfig extends Config<StudioPlugin> {
}

class ContentHubStudioCloudinaryPlugin extends StudioPlugin {
  declare Config: ContentHubStudioCloudinaryPluginConfig;

  static readonly xtype: string = "com.coremedia.labs.plugins.adapters.cloudinary.ContentHubStudioCloudinaryPlugin";

  constructor(config: Config<ContentHubStudioCloudinaryPlugin> = null) {
    super(ConfigUtils.apply(Config(ContentHubStudioCloudinaryPlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentHub_properties),
          source: resourceManager.getResourceBundle(null, ContentHubCloudinary_properties),
        }),
      ],

    }), config));
  }
}

export default ContentHubStudioCloudinaryPlugin;

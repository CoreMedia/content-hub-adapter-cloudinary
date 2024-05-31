import SvgIconUtil from "@coremedia/studio-client.base-models/util/SvgIconUtil";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import icon from "./icons/cloudinary_16.svg";

/**
 * Interface values for ResourceBundle "ContentHubCloudinary".
 * @see ContentHubCloudinary_properties
 */
interface ContentHubCloudinary_properties {

/**
 *Cloudinary
 */
  author_header: string;
  lastModified_header: string;
  folder_type_cloudinary_folder_name: string;
  folder_type_cloudinary_folder_icon: string;
  adapter_type_cloudinary_name: string;
  adapter_type_cloudinary_icon: string;
  item_type_cloudinary_file_name: string;
  item_type_cloudinary_file_icon: string;

  metadata_sectionName: string;
  text_sectionItemKey: string;
  size_sectionItemKey: string;
  author_sectionItemKey: string;
  published_sectionItemKey: string;
  lastModified_sectionItemKey: string;
  link_sectionItemKey: string;
  dimension_sectionItemKey: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubCloudinary".
 * @see ContentHubCloudinary_properties
 */
const ContentHubCloudinary_properties: ContentHubCloudinary_properties = {
  author_header: "Author",
  lastModified_header: "Last Modified",
  folder_type_cloudinary_folder_name: "Folder",
  folder_type_cloudinary_folder_icon: CoreIcons_properties.folder,
  adapter_type_cloudinary_name: "Cloudinary",
  adapter_type_cloudinary_icon: SvgIconUtil.getIconStyleClassForSvgIcon(icon),
  item_type_cloudinary_file_name: "File",
  item_type_cloudinary_file_icon: CoreIcons_properties.type_external_content,

  metadata_sectionName: "Metadata",
  text_sectionItemKey: "Text",
  size_sectionItemKey: "Size",
  author_sectionItemKey: "Author",
  published_sectionItemKey: "Published",
  lastModified_sectionItemKey: "Last modified",
  link_sectionItemKey: "Link",
  dimension_sectionItemKey: "Dimension",
};

export default ContentHubCloudinary_properties;

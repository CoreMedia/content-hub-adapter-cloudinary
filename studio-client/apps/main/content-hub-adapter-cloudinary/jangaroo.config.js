/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  sencha: {
    name: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-cloudinary",
    namespace: "com.coremedia.labs.plugins.adapters.cloudinary",
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.cloudinary.ContentHubStudioCloudinaryPlugin",
        name: "Content Hub",
      },
    ],
  },
};

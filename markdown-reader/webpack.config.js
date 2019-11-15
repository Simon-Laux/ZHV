const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin')
const HtmlWebpackInlineSourcePlugin = require('html-webpack-inline-source-plugin');

module.exports = {
  entry: './src/index.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js'
  },
  mode: 'production',
  plugins: [
    new HtmlWebpackPlugin({
        inlineSource: '.(js|css)$', // embed all javascript and css inline
        title: "Markdown Reader",
        filename: "markdown-reader.html"
      }),
    new HtmlWebpackInlineSourcePlugin()
  ]  
};
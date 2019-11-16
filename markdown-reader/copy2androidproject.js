const fs = require ('fs-extra');
const { join } = require ('path');

async function main (){
    const asset_folder = join(__dirname, '../app/src/main/assets')
    await fs.ensureDir(asset_folder)
    await fs.copyFile(join(__dirname, './dist/markdown-reader.html'), join(asset_folder,"markdown-reader.html"))
    console.log("Copied result to android project")
}

main()
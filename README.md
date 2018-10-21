# Setup Instructions
## Backend
The backend is built with Java, Servlets, and MySQL. You will need to use Maven for resolving dependencies.
### Using Maven
Maven allows projects to be plug and play. The IDE will detect the `pom.xml` configuration without you having to do much.
If you would like to add a dependency, find it on [Maven](https://mvnrepository.com) and paste in the `<dependency>` XML snippet provided on the website.

### Server Set Up
For consistency with paths, we want to deploy on `localhost:8080/` instead of `localhost:8080/SCroup`.
To do this, go to the Servers folder in the Project Explorer, find `server.xml`
Edit the `<Context/>` tag `path=` value to be `path=""`

**NOTE:** The first build will take significantly longer than subsequent builds. *Be patient!*

## Frontend
The frontend is built with React. You will need `npm` to compile the source files which can be found in `src/main/webapp`.
### Installing Node Modules
1. Make sure to have [npm](https://www.npmjs.com/get-npm)
2. `cd src/main/webapp`
3. `npm install`
4. `npm run build:dev` or `npm run build:prod` (details below)

### Build
During active frontend development, use `npm run build:dev` to quickly build changes; however this is a large and unoptimized file that should not be used in production

For deployment, use `npm run build:prod` which will shake out unnecessary sub-modules and minify the code
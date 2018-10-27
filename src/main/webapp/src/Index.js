import "@babel/polyfill";

import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter, Route, Switch} from "react-router-dom";

import HeaderBar from "./pages/HeaderBar";
import MuiThemeProvider from "@material-ui/core/styles/MuiThemeProvider";
import {createMuiTheme} from "@material-ui/core/es/styles";
import Login from "./pages/Login";
import CssBaseline from "@material-ui/core/CssBaseline/CssBaseline";


class Index extends React.Component {
    static get base() {
        return "/";
    }

    render() {
        const theme = createMuiTheme({
            palette: {
                primary: {
                    main: "#991B1E"
                },
                secondary: {
                    main: "#FFCC00"
                }
            },
            typography: {
                useNextVariants: true
            }
        });
        return (
            <React.Fragment>
                <CssBaseline/>
                <MuiThemeProvider theme={theme}>
                    <HeaderBar/>
                    <main>
                        <BrowserRouter>
                            <Switch>
                                <Route exact path={Index.base} component={Login}/>
                            </Switch>
                        </BrowserRouter>
                    </main>
                </MuiThemeProvider>
            </React.Fragment>
        );
    }
}

ReactDOM.render(
    <Index/>,
    document.getElementById("app")
);
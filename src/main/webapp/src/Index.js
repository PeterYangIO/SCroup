import "@babel/polyfill";

import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter, Route, Switch} from "react-router-dom";

import MuiThemeProvider from "@material-ui/core/styles/MuiThemeProvider";
import {createMuiTheme} from "@material-ui/core/es/styles";
import Login from "./pages/Login";
import CssBaseline from "@material-ui/core/CssBaseline/CssBaseline";
import Home from "./pages/Home";
import GroupDashboard from "./pages/GroupDashboard";
import UserProfile from "./pages/UserProfile";

class Index extends React.Component {
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
            },
            overrides: {
                MuiFormControl: {
                    root: {
                        width: "100%"
                    }
                },
                MuiInput: {
                    root: {
                        width: "100%",
                        marginBottom: "0.5rem"
                    }
                },
                MuiCardActions: {
                    root: {
                        justifyContent: "flex-end"
                    }
                }
            }
        });
        return (
            <>
                <CssBaseline/>
                <MuiThemeProvider theme={theme}>
                    <BrowserRouter>
                        <Switch>
                            <Route exact path="/" component={Login}/>
                            <Route path="/home" component={Home}/>
                            <Route path="/dashboard" component={GroupDashboard} />
                            <Route path="/profile" component={UserProfile} />
                        </Switch>
                    </BrowserRouter>
                </MuiThemeProvider>
            </>
        );
    }
}

ReactDOM.render(
    <Index/>,
    document.getElementById("app")
);
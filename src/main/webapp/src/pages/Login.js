import React from "react";
import Tabs from "@material-ui/core/Tabs/Tabs";
import Tab from "@material-ui/core/Tab/Tab";
import AppBar from "@material-ui/core/AppBar/AppBar";
import LoginForm from "../components/Login/LoginForm";
import SignUpForm from "../components/Login/SignUpForm";

export default class Login extends React.Component {
    handleTabChange = (event, value) => {
        this.setState({value});
    };

    constructor(props) {
        super(props);

        this.state = {
            value: 0
        };
    }

    render() {
        const {value} = this.state;
        return (
            <div style={{
                width: "50%",
                margin: "auto",
                marginTop: "2rem"
            }}>
                <AppBar position="static" color="secondary">
                    <Tabs
                        value={value}
                        onChange={this.handleTabChange}
                        indicatorColor="primary">
                        <Tab label="Login"/>
                        <Tab label="Sign Up"/>
                    </Tabs>
                </AppBar>
                {
                    value === 0 && <LoginForm/>
                }
                {
                    value === 1 && <SignUpForm/>
                }
            </div>
        );
    }
}
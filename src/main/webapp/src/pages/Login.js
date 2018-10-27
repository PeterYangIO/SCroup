import React from "react";
import Tabs from "@material-ui/core/Tabs/Tabs";
import Tab from "@material-ui/core/Tab/Tab";
import AppBar from "@material-ui/core/AppBar/AppBar";
import LoginForm from "../components/Login/LoginForm";
import SignUpForm from "../components/Login/SignUpForm";
import withStyles from "@material-ui/core/styles/withStyles";
import HeaderBar from "../components/common/HeaderBar";

@withStyles({
    root: {
        maxWidth: "400px",
        margin: "auto",
        marginTop: "2rem"
    }
})
export default class Login extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: 0
        };
    }

    handleTabChange = (event, value) => {
        this.setState({value});
    };

    render() {
        const {value} = this.state;
        const {classes} = this.props;
        return (
            <>
                <HeaderBar title="Login"/>
                <main>
                    <div className={classes.root}>
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
                </main>
            </>
        );
    }
}
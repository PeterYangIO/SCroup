import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";
import withStyles from "@material-ui/core/styles/withStyles";
import NetworkRequest from "../../util/NetworkRequest";
import {withRouter} from "react-router-dom";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";
import IconButton from "@material-ui/core/IconButton/IconButton";
import CloseIcon from "@material-ui/icons/Close";

@withRouter
@withStyles({
    container: {
        borderTopRightRadius: 0,
        borderTopLeftRadius: 0
    },
    forgotPassword: {
        width: "100%"
    }
})
export default class LoginForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            email: "",
            password: "",

            forgotPassword: false,
            showSnackbar: false
        };
    }

    continueAsGuest = () => {
        this.props.history.push("/home");
    };

    forgotPassword = async () => {
        try {
            const formData = new FormData();
            formData.append("email", this.state.email);
            const response = await NetworkRequest.delete("api/register", formData, false);
            if (response.ok) {
                this.setState({
                    showSnackbar: true
                });
            }
            else {
                alert("Unknown error");
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    login = async () => {
        try {
            const response = await NetworkRequest.post("api/login", {
                email: this.state.email,
                password: this.state.password
            });
            if (response.ok) {
                const data = await response.json();
                sessionStorage.setItem("authToken", data.authToken);
                sessionStorage.setItem("user", JSON.stringify(data));
                this.props.history.push("/home");
            }
            else if (response.status === 401) {
                alert("Invalid credentials");
            }
            else {
                alert("Unknown error");
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    submit = async (event) => {
        event.preventDefault();

        this.state.forgotPassword
            ? await this.forgotPassword()
            : await this.login();
    };

    toggleForgotPassword = () => {
        this.setState({
            forgotPassword: !this.state.forgotPassword
        });
    };

    render() {
        const {email, password, forgotPassword, showSnackbar} = this.state;
        const {classes} = this.props;

        return (
            <>
                <Card className={classes.container}>
                    <form onSubmit={this.submit}>
                        <CardContent>
                            <TextField
                                required
                                type="email"
                                label="Email"
                                name="email"
                                value={email}
                                onChange={this.handleChange}/>
                            {
                                !forgotPassword &&
                                <TextField
                                    required
                                    type="password"
                                    label="Password"
                                    name="password"
                                    value={password}
                                    onChange={this.handleChange}/>
                            }
                            <Button
                                className={classes.forgotPassword}
                                type="button"
                                size="small"
                                onClick={this.toggleForgotPassword}>
                                {forgotPassword ? "Go Back" : "Forgot Password"}
                            </Button>
                        </CardContent>
                        <CardActions>
                            <Button
                                type="button"
                                onClick={() => this.props.history.push("/home")}>
                                Continue as a Guest
                            </Button>
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary">
                                {forgotPassword ? "Submit" : "Login"}
                            </Button>
                        </CardActions>
                    </form>
                </Card>

                <Snackbar
                    anchorOrigin={{
                        vertical: "bottom", horizontal: "left"
                    }}
                    open={showSnackbar}
                    onClose={() => this.setState({showSnackbar: false})}
                    message={<span>Check your email for password reset</span>}
                    action={
                        <IconButton
                            color="inherit"
                            onClick={() => this.setState({showSnackbar: false})}>
                            <CloseIcon/>
                        </IconButton>
                    }/>
            </>
        );
    }
}
import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";
import withStyles from "@material-ui/core/styles/withStyles";
import NetworkRequest from "../../util/NetworkRequest";
import {withRouter} from "react-router-dom";

@withRouter
@withStyles({
    container: {
        borderTopRightRadius: 0,
        borderTopLeftRadius: 0
    }
})
export default class LoginForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            email: "",
            password: ""
        };
    }

    continueAsGuest = () => {
        this.props.history.push("/home");
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    submit = async (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append("email", this.state.email);
        formData.append("password", this.state.password);

        const response = await NetworkRequest.post("api/login", formData, false);
        if (response.ok) {
            // const data = await response.json();
            // sessionStorage.setItem("accessToken", data.accessToken);
            this.props.history.push("/home");
        }
        else if (response.status === 401) {
            alert("Invalid credentials");
        }
        else {
            alert("Server error");
        }
    };

    render() {
        const {email, password} = this.state;
        const {classes} = this.props;

        return (
            <Card className={classes.container}>
                <form onSubmit={this.submit} id="login-form">
                    <CardContent>
                        <TextField
                            required
                            type="email"
                            label="Email"
                            name="email"
                            value={email}
                            onChange={this.handleChange}/>
                        <TextField
                            required
                            type="password"
                            label="Password"
                            name="password"
                            value={password}
                            onChange={this.handleChange}/>
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
                            Login
                        </Button>
                    </CardActions>
                </form>
            </Card>
        );
    }
}
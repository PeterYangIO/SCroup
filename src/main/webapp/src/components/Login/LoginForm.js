import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";
import withStyles from "@material-ui/core/styles/withStyles";
import NetworkRequest from "../../util/NetworkRequest";

@withStyles({
    container: {
        borderTopRightRadius: 0,
        borderTopLeftRadius: 0
    },
    input: {
        width: "100%",
        paddingBottom: "0.5rem"
    },
    buttons: {
        justifyContent: "flex-end"
    }
})
export default class LoginForm extends React.Component {
    submit = async (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append("email", this.state.email);
        formData.append("password", this.state.password);

        const response = await NetworkRequest.post("api/login", formData, false);
        if (response.ok) {
            alert("Good");
        }
        else {
            alert("Not good");
        }
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    constructor(props) {
        super(props);

        this.state = {
            email: "",
            password: ""
        };
    }

    render() {
        const {email, password} = this.state;
        const {classes} = this.props;

        return (
            <form onSubmit={this.submit}>
                <Card className={classes.container}>
                    <CardContent>
                        <TextField
                            className={classes.input}
                            type="email"
                            label="Email"
                            name="email"
                            value={email}
                            onChange={this.handleChange}/>
                        <TextField
                            className={classes.input}
                            type="password"
                            label="Password"
                            name="password"
                            value={password}
                            onChange={this.handleChange}/>
                    </CardContent>
                    <CardActions className={classes.buttons}>
                        <Button>Continue as a Guest</Button>
                        <Button variant="contained" color="primary" type="submit">Login</Button>
                    </CardActions>
                </Card>
            </form>
        );
    }
}
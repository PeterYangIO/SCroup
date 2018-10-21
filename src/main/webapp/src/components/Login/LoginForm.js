import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";

export default class LoginForm extends React.Component {
    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };
    submit = (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append("email", this.state.email);
        formData.append("password", this.state.password);

        fetch("api/login", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    alert("Good");
                }
                else {
                    alert("Not good");
                }
            })
            .catch(() => {
                alert("Very bad");
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

        return (
            <form onSubmit={this.submit}>
                <Card>
                    <CardContent>
                        <TextField
                            type="email"
                            label="Email"
                            name="email"
                            value={email}
                            onChange={this.handleChange}/>
                        <TextField
                            type="password"
                            label="Password"
                            name="password"
                            value={password}
                            onChange={this.handleChange}/>
                    </CardContent>
                    <CardActions>
                        <Button variant="contained" color="primary" type="submit">Login</Button>
                        <Button>Continue as a Guest</Button>
                    </CardActions>
                </Card>
            </form>
        );
    }
}
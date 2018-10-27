import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";
import withStyles from "@material-ui/core/styles/withStyles";

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
export default class SignUpForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            email: "",
            password: "",
            first_name: "",
            last_name: "",
            year: "",
            major: ""
        };
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    render() {
        const {email, password, first_name, last_name, year, major} = this.state;
        const {classes} = this.props;

        return (
            <form>
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
                        <TextField
                            className={classes.input}
                            label="First Name"
                            name="first_name"
                            value={first_name}
                            onChange={this.handleChange}/>
                        <TextField
                            className={classes.input}
                            label="Last Name"
                            name="last_name"
                            value={last_name}
                            onChange={this.handleChange}/>
                        <TextField
                            className={classes.input}
                            label="Year"
                            name="year"
                            value={year}
                            onChange={this.handleChange}/>
                        <TextField
                            className={classes.input}
                            label="Major"
                            name="major"
                            value={major}
                            onChange={this.handleChange}/>
                    </CardContent>
                    <CardActions className={classes.buttons}>
                        <Button variant="contained" color="primary">Sign Up</Button>
                    </CardActions>
                </Card>
            </form>
        );
    }
}
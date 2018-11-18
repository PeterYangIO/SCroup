import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Card from "@material-ui/core/Card/Card";
import CardContent from "@material-ui/core/CardContent/CardContent";
import CardActions from "@material-ui/core/CardActions/CardActions";
import withStyles from "@material-ui/core/styles/withStyles";
import NetworkRequest from "../../util/NetworkRequest";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";
import IconButton from "@material-ui/core/IconButton/IconButton";
import CloseIcon from "@material-ui/icons/Close";


@withStyles({
    container: {
        borderTopRightRadius: 0,
        borderTopLeftRadius: 0
    }
})
export default class SignUpForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            email: "",
            password: "",
            firstName: "",
            lastName: "",
            year: "",
            major: "",

            showSnackbar: false
        };
    }

    handleChange = (event) => {
        const name = event.target.name;
        const value = name === "email" ? event.target.value.toLowerCase() : event.target.value;
        this.setState({
            [name]: value
        });
    };

    submit = async (event) => {
        event.preventDefault();

        try {
            const response = await NetworkRequest.post("api/register", {
                email: this.state.email,
                password: this.state.password,
                firstName: this.state.firstName,
                lastName: this.state.lastName,
                year: this.state.year,
                major: this.state.major
            });
            if (response.ok) {
                this.setState({
                    showSnackbar: true
                });
            }
            else if (response.status === 400){
            	alert("Account has been registered!");
            }else{
            	alert("Unknown error");
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    render() {
        const {email, password, firstName, lastName, year, major} = this.state;
        const {classes} = this.props;

        return (
            <>
                <Card className={classes.container}>
                    <form onSubmit={this.submit}>
                        <CardContent>
                            <TextField
                                required
                                inputProps={{
                                    pattern: "[-_a-zA-Z0-9]{3,8}@usc\\.edu"
                                }}
                                type="email"
                                label="Email"
                                name="email"
                                value={email}
                                onChange={this.handleChange}
                                helperText="Use a USC email"/>
                            <TextField
                                required
                                inputProps={{
                                    pattern: "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
                                }}
                                type="password"
                                label="Password"
                                name="password"
                                value={password}
                                onChange={this.handleChange}
                                helperText="At least 8 characters with 1 letter and number"/>
                            <TextField
                                required
                                label="First Name"
                                name="firstName"
                                value={firstName}
                                onChange={this.handleChange}/>
                            <TextField
                                required
                                label="Last Name"
                                name="lastName"
                                value={lastName}
                                onChange={this.handleChange}/>
                            <TextField
                                label="Year"
                                name="year"
                                value={year}
                                onChange={this.handleChange}/>
                            <TextField
                                label="Major"
                                name="major"
                                value={major}
                                onChange={this.handleChange}/>
                        </CardContent>
                        <CardActions>
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary">
                                Sign Up
                            </Button>
                        </CardActions>
                    </form>
                </Card>

                <Snackbar
                    anchorOrigin={{
                        vertical: "bottom", horizontal: "left"
                    }}
                    open={this.state.showSnackbar}
                    onClose={() => this.setState({showSnackbar: false})}
                    message={<span>Account created successfully</span>}
                    action={
                        <>
                            <Button
                                color="secondary"
                                size="small"
                                onClick={this.props.login}>
                                Login
                            </Button>
                            <IconButton
                                color="inherit"
                                onClick={() => this.setState({showSnackbar: false})}>
                                <CloseIcon/>
                            </IconButton>
                        </>
                    }
                />
            </>
        );
    }
}
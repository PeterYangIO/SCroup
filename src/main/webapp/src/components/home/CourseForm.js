import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import withStyles from "@material-ui/core/styles/withStyles";

@withStyles({
    half: {
        width: "45%"
    }
})
export default class CourseForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            department: props.department ? props.department : "",
            number: props.number ? props.number : "",
            name: props.name ? props.name : ""
        };
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    submit = async () => {
        const submitData = {
            department: this.state.department,
            number: this.state.number,
            name: this.state.name
        };

        await this.props.submit("/api/courses", submitData);
    };

    render() {
        const {classes} = this.props;

        return (
            <>
                <TextField
                    className={classes.half}
                    name="department"
                    label="Department"
                    placeholder="CSCI"
                    value={this.state.department}
                    onChange={this.handleChange}/>
                <TextField
                    className={classes.half}
                    type="number"
                    name="number"
                    label="Number"
                    placeholder="103"
                    value={this.state.number}
                    onChange={this.handleChange}/>
                <TextField
                    name="name"
                    label="Name"
                    placeholder="Introduction to Computer Science"
                    value={this.state.name}
                    onChange={this.handleChange}/>
            </>
        );
    }
}
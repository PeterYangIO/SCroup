import React from "react";
import FormControl from "@material-ui/core/FormControl/FormControl";
import InputLabel from "@material-ui/core/InputLabel/InputLabel";
import Select from "@material-ui/core/Select/Select";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import TextField from "@material-ui/core/TextField/TextField";
import withStyles from "@material-ui/core/styles/withStyles";

@withStyles({
    half: {
        width: "45%"
    }
})
export default class StudyGroupForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            courseId: props.courseId ? props.courseId: 1,
            capacity: props.capacity ? props.capacity : "",
            location: props.location ? props.location : "",
            topic: props.topic !== undefined ? props.topic : 0,
            professor: props.professor ? props.professor : "",
            start: props.start ? props.start : "",
            end: props.end ? props.end : ""
        };
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    submit = async () => {
        const submitData = {
            courseId: this.state.courseId,
            topic: this.state.topic
        };
        if (this.state.capacity) {
            submitData.capacity = parseInt(this.state.capacity);
        }
        if (this.state.location) {
            submitData.location = this.state.location;
        }
        if (this.state.professor) {
            submitData.professor = this.state.professor;
        }
        if (this.state.start) {
            submitData.start = new Date(this.state.start).toISOString();
        }
        if (this.state.end) {
            submitData.end = new Date(this.state.end).toISOString();
        }

        await this.props.submit("/api/study-groups", submitData);
    };

    render() {
        const {classes} = this.props;

        return (
            <>
                <FormControl className={classes.half}>
                    <InputLabel htmlFor="courseId">Course</InputLabel>
                    <Select
                        required
                        value={this.state.courseId}
                        onChange={this.handleChange}
                        inputProps={{
                            name: "courseId",
                            id: "courseId"
                        }}>
                        {
                            Object.keys(this.props.departments).sort().map(department =>
                                this.props.departments[department].map(course => (
                                    <MenuItem
                                        value={course.id}>
                                        {course.department}-{course.number}
                                    </MenuItem>
                                ))
                            )
                        }
                    </Select>
                </FormControl>
                <TextField
                    className={classes.half}
                    type="number"
                    name="capacity"
                    label="Capacity"
                    value={this.state.capacity}
                    onChange={this.handleChange}/>

                <FormControl className={classes.half}>
                    <InputLabel htmlFor="topic">Topic</InputLabel>
                    <Select
                        value={this.state.topic}
                        onChange={this.handleChange}
                        inputProps={{
                            name: "topic",
                            id: "topic"
                        }}>
                        <MenuItem value={0}>General Study</MenuItem>
                        <MenuItem value={1}>Homework</MenuItem>
                        <MenuItem value={2}>Quiz</MenuItem>
                        <MenuItem value={3}>Midterm</MenuItem>
                        <MenuItem value={4}>Final</MenuItem>
                    </Select>
                </FormControl>
                <TextField
                    className={classes.half}
                    name="professor"
                    label="Professor"
                    value={this.state.professor}
                    onChange={this.handleChange}/>

                <TextField
                    name="location"
                    label="Location"
                    value={this.state.location}
                    onChange={this.handleChange}/>

                <TextField
                    className={classes.half}
                    type="datetime-local"
                    name="start"
                    label="Start"
                    value={this.state.start}
                    onChange={this.handleChange}
                    InputLabelProps={{
                        shrink: true
                    }}/>
                <TextField
                    className={classes.half}
                    type="datetime-local"
                    name="end"
                    label="End"
                    value={this.state.end}
                    onChange={this.handleChange}
                    InputLabelProps={{
                        shrink: true
                    }}/>
            </>
        );
    }
}
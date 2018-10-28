import React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button/Button";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog/Dialog";
import Tabs from "@material-ui/core/Tabs/Tabs";
import Tab from "@material-ui/core/Tab/Tab";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import TextField from "@material-ui/core/TextField/TextField";
import FormControl from "@material-ui/core/FormControl/FormControl";
import InputLabel from "@material-ui/core/InputLabel/InputLabel";
import Select from "@material-ui/core/Select/Select";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import NetworkRequest from "../../util/NetworkRequest";

@withStyles({
    content: {
        marginTop: "1rem",
        display: "flex",
        flexWrap: "wrap",
        justifyContent: "space-between"
    },
    fab: {
        position: "absolute",
        bottom: "1rem",
        right: "1rem"
    },
    half: {
        width: "45%"
    }
})
export default class CreateFab extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            open: false,
            tab: 0,

            department: "",
            number: "",
            name: "",

            courseId: 1,
            capacity: "",
            location: "",
            topic: 0,
            professor: "",
            start: "",
            end: ""
        }
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleClickOpen = () => {
        this.setState({
            open: true,
            tab: this.props.course === undefined ? 0 : 1,
            courseId: this.props.course === undefined ? -1 : this.props.course.id
        });
    };

    handleClose = () => {
        this.setState({open: false});
    };

    handleTabChange = (event, tab) => {
        this.setState({tab});
    };

    submit = async () => {
        let url, submitData;
        // Create a course
        if (this.state.tab === 0) {
            url = "/api/courses";
            submitData = {
                department: this.state.department,
                number: this.state.number,
                name: this.state.name
            }
        }
        // Create a study group
        else {
            url = "/api/study-groups";
            submitData = {
                courseId: this.state.courseId,
                capacity: this.state.capacity,
                location: this.state.location,
                topic: this.state.topic,
                professor: this.state.professor,
                start: new Date(this.state.start).toISOString(),
                end: new Date(this.state.end).toISOString()
            }
        }

        try {
            const response = await NetworkRequest.post(url, submitData);
            if (response.ok) {
                this.handleClose();
                alert("Success!");
            }
            else {
                alert("Invalid input");
            }
        }
        catch (exception) {
            console.error(error);
        }
    };

    render() {
        const {open, tab} = this.state;
        const {classes} = this.props;

        return (
            <>
                <Button onClick={this.handleClickOpen} variant="fab" color="secondary" className={classes.fab}>
                    <AddIcon/>
                </Button>

                <Dialog
                    open={open}
                    onClose={this.handleClose}>
                    <Tabs
                        value={tab}
                        onChange={this.handleTabChange}>
                        <Tab label="Course"/>
                        <Tab label="Group"/>
                    </Tabs>
                    <DialogContent className={classes.content}>
                        {
                            tab === 0 && (
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
                            )
                        }
                        {
                            tab === 1 && (
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
                            )
                        }
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => this.handleClose()}>Cancel</Button>
                        <Button color="primary" onClick={() => this.submit()}>Submit</Button>
                    </DialogActions>
                </Dialog>
            </>
        )
    }
}
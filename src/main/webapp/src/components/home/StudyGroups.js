import React from "react";
import Typography from "@material-ui/core/Typography/Typography";
import NetworkRequest from "../../util/NetworkRequest";
import ExpansionPanel from "@material-ui/core/ExpansionPanel/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary/ExpansionPanelSummary";
import ExpansionPanelDetails from "@material-ui/core/ExpansionPanelDetails/ExpansionPanelDetails";
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import withStyles from "@material-ui/core/styles/withStyles";
import moment from "moment";
import ExpansionPanelActions from "@material-ui/core/ExpansionPanelActions/ExpansionPanelActions";
import Divider from "@material-ui/core/Divider/Divider";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import FormGroup from "@material-ui/core/FormGroup/FormGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox/Checkbox";
import InputLabel from "@material-ui/core/InputLabel/InputLabel";
import Select from "@material-ui/core/Select/Select";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import FormControl from "@material-ui/core/FormControl/FormControl";
import {Link} from "react-router-dom";
import Dialog from "@material-ui/core/Dialog/Dialog";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import StudyGroupForm from "./StudyGroupForm";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";

@withStyles(theme => ({
    column: {
        width: "33.3%"
    },
    dialog: {
        marginTop: "1rem",
        display: "flex",
        flexWrap: "wrap",
        justifyContent: "space-between"
    },
    filters: {
        display: "flex",
        flexWrap: "wrap",
        justifyContent: "space-between",
        marginBottom: "1rem"
    },
    filterWidth: {
        width: "21%"
    },
    inlineText: {
        display: "inline-block"
    },
    panelDetails: {
        display: "block"
    },
    secondaryText: {
        color: theme.palette.text.secondary
    }
}))
export default class StudyGroups extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            webSocket: undefined,

            authenticated: false,
            studyGroups: undefined,
            expandedPanel: -1,

            // Edit modal
            showEditDialog: false,
            groupSelectedForEditing: undefined,

            // Filters
            capacityMin: "",
            capacityMax: "",
            hideFull: false,
            location: "",
            topic: "",
            professor: "",
            after: moment().format("YYYY-MM-DDTHH:mm"),
            before: ""
        };

        this.studyGroupFormRef = React.createRef();
    }

    async componentDidMount() {
        await this.getData();
    }

    /**
     * Gets data from api and opens WebSocket for any join / leave updates
     */
    async componentDidUpdate(prevProps) {
        const courseId = this.props.course ? this.props.course.id : -1;
        const prevCourseId = prevProps.course ? prevProps.course.id : -1;
        if (courseId !== prevCourseId) {
            await this.getData();
            this.connectToWebSocket();
        }
    }

    /**
     * Close WebSocket to current group before moving to next group
     */
    componentWillUpdate(prevProps) {
        const courseId = this.props.course ? this.props.course.id : -1;
        const prevCourseId = prevProps.course ? prevProps.course.id : -1;
        if (courseId !== prevCourseId) {
            this.state.webSocket && this.state.webSocket.close();
        }
    }

    /**
     * Connects to the study-groups WebSocket for this course id. This is
     * similar to having multiple "chat rooms" where each course
     * has its own room with clients connected to it at any time.
     */
    connectToWebSocket = () => {
        if (this.props.course === undefined) {
            return
        }
        const webSocket = new WebSocket(`ws://${window.location.host}/study-groups/${this.props.course.id}`);
        webSocket.onopen = () => {
            webSocket.send(sessionStorage.getItem("authToken"));
        };

        webSocket.onmessage = async (event) => {
            const data = JSON.parse(event.data);
            switch (data.message) {
                case "AUTHENTICATION":
                    this.setState({
                        authenticated: data.success
                    });
                    break;
                case "REFRESH":
                    await this.getData();
                    break;
                case "INVALID":
                    alert("Could not process your request");
                    break;
                default:
                    console.error(data.message, "not implemented");
            }
        };

        webSocket.onclose = () => {
            console.log(`closed ${webSocket.url}`);
        };

        this.setState({webSocket});
    };

    /**
     * Closes WebSocket before navigating away
     */
    componentWillUnmount() {
        this.state.webSocket && this.state.webSocket.close();
    }

    closeEditDialog = () => {
        this.setState({
            showEditDialog: false
        });
    };

    displayEditModal = (groupSelectedForEditing) => {
        this.setState({
            showEditDialog: true,
            groupSelectedForEditing
        });
    };

    /**
     * Makes request to /api/study-groups (not the WebSocket)
     * with filter parameters to get list of study groups
     */
    getData = async () => {
        if (this.props.course === undefined) {
            return;
        }
        try {
            const {capacityMin, capacityMax, hideFull, location, topic, professor, after, before} = this.state;
            const filterParameters = {
                courseId: this.props.course.id
            };
            if (typeof(capacityMin) === "number")
                filterParameters.capacityMin = capacityMin;
            if (typeof(capacityMax) === "number")
                filterParameters.capacityMax = capacityMax;
            if (hideFull)
                filterParameters.hideFull = hideFull;
            if (location)
                filterParameters.location = location;
            if (typeof(topic) === "number")
                filterParameters.topic = topic;
            if (professor)
                filterParameters.professor = professor;
            if (after)
                filterParameters.after = new Date(after).toISOString();
            if (before)
                filterParameters.before = new Date(before).toISOString();

            const response = await NetworkRequest.get("/api/study-groups", filterParameters);
            if (response.ok) {
                const studyGroups = await response.json();
                this.setState({studyGroups})
            }
            else {
                console.error(response.status, response);
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    /**
     * Handle changing of filter option (checkbox, dropdown, text)
     */
    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.type === "checkbox" ? !this.state[event.target.name] : event.target.value
        }, this.getData);
    };

    /**
     * Handle open / closing of selected group panel
     */
    handlePanel = (expandedPanel) => {
        this.setState({
            expandedPanel: this.state.expandedPanel === expandedPanel ? -1 : expandedPanel
        });
    };

    submitChildData = async (url, submitData) => {
        try {
            submitData.id = this.state.groupSelectedForEditing.id;
            const response = await NetworkRequest.put(url, submitData);
            if (response.ok) {
                alert("Success");
            }
            else {
                alert("Invalid input");
            }
        }
        catch (exception) {
            console.error(error);
        }
    };

    submitGroupEdits = async () => {
        await this.studyGroupFormRef.current.submit();
    };

    /**
     * Communicates with WebSocket to join / leave group
     */
    toggleJoin = (groupId, joined) => {
        this.state.webSocket.send(
            JSON.stringify({
                method: joined ? "leave" : "join",
                data: {groupId}
            })
        );
    };

    render() {
        const {classes, course} = this.props;

        if (course === undefined) {
            return (
                <Typography component="p" variant="body1">
                    Please select a course from the left to browse study groups.
                </Typography>
            );
        }

        return (
            <>
                <Typography component="h2" variant="h2">{course.department}-{course.number}</Typography>
                <Typography component="h5" variant="h5" gutterBottom>{course.name}</Typography>
                <div className={classes.filters}>
                    <TextField
                        className={classes.filterWidth}
                        type="number"
                        name="capacityMin"
                        label="Min Capacity"
                        value={this.state.capacityMin}
                        onChange={this.handleChange}/>
                    <TextField
                        className={classes.filterWidth}
                        type="number"
                        name="capacityMax"
                        label="Max Capacity"
                        value={this.state.capacityMax}
                        onChange={this.handleChange}/>
                    <FormGroup className={classes.filterWidth}>
                        <FormControlLabel
                            control={
                                <Checkbox checked={this.state.hideFull} name="hideFull" onChange={this.handleChange}/>
                            }
                            label="Hide Full"/>
                    </FormGroup>
                    <TextField
                        className={classes.filterWidth}
                        name="location"
                        label="Location"
                        value={this.state.location}
                        onChange={this.handleChange}/>
                    <FormControl className={classes.filterWidth}>
                        <InputLabel htmlFor="topic">Topic</InputLabel>
                        <Select
                            value={this.state.topic}
                            onChange={this.handleChange}
                            inputProps={{
                                name: "topic",
                                id: "topic"
                            }}>
                            <MenuItem value=""><em>Any</em></MenuItem>
                            <MenuItem value={0}>General Study</MenuItem>
                            <MenuItem value={1}>Homework</MenuItem>
                            <MenuItem value={2}>Quiz</MenuItem>
                            <MenuItem value={3}>Midterm</MenuItem>
                            <MenuItem value={4}>Final</MenuItem>
                        </Select>
                    </FormControl>
                    <TextField
                        className={classes.filterWidth}
                        name="professor"
                        label="Professor"
                        value={this.state.professor}
                        onChange={this.handleChange}/>
                    <TextField
                        className={classes.filterWidth}
                        type="datetime-local"
                        name="after"
                        label="After"
                        value={this.state.after}
                        onChange={this.handleChange}
                        InputLabelProps={{
                            shrink: true
                        }}/>
                    <TextField
                        className={classes.filterWidth}
                        type="datetime-local"
                        name="before"
                        label="Before"
                        value={this.state.before}
                        onChange={this.handleChange}
                        InputLabelProps={{
                            shrink: true
                        }}/>
                </div>

                {
                    this.state.studyGroups && this.state.studyGroups.length === 0 &&
                    <Typography>No study groups for this course.</Typography>
                }
                {
                    this.state.studyGroups && this.state.studyGroups.length > 0 && this.state.studyGroups.map(item => (
                        <ExpansionPanel
                            key={item.id}
                            expanded={this.state.expandedPanel === item.id}
                            onChange={() => this.handlePanel(item.id)}>
                            <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                <div className={classes.column}>
                                    <Typography
                                        className={`${classes.secondaryText} ${classes.inlineText}`}
                                        style={{marginRight: "1rem"}}>
                                        {item.size}/{item.capacity ? item.capacity : "∞"}
                                    </Typography>
                                    <Typography className={classes.inlineText}>
                                        {item.ownerId}'s Group
                                    </Typography>
                                </div>
                                <div className={classes.column}>
                                    <Typography className={classes.inlineText}>
                                        {["General Study", "Homework", "Quiz", "Midterm", "Final"][item.topic]}
                                    </Typography>
                                </div>
                                <div className={classes.column}>
                                    <Typography className={classes.secondaryText}>
                                        {moment(item.start).format("M/D, h:mma")} – {moment(item.end).format("h:mma")}
                                    </Typography>
                                </div>
                            </ExpansionPanelSummary>
                            <ExpansionPanelDetails className={classes.panelDetails}>
                                <Typography>Location: {item.location ? item.location : "N/A"}</Typography>
                                <Typography>Professor: {item.professor ? item.professor : "N/A"}</Typography>
                            </ExpansionPanelDetails>
                            <Divider/>
                            {
                                this.state.authenticated &&
                                <ExpansionPanelActions>
                                    {
                                        item.joined &&
                                        <Button
                                            color="primary" size="small" variant="contained"
                                            component={Link} to="/">
                                            Chat Room
                                        </Button>
                                    }
                                    {
                                        item.ownerId === JSON.parse(sessionStorage.getItem("user")).id &&
                                        <Button color="primary" size="small" onClick={() => this.displayEditModal(item)}>
                                            Edit
                                        </Button>
                                    }
                                    <Button color="primary" size="small"
                                            onClick={() => this.toggleJoin(item.id, item.joined)}>
                                        {item.joined ? "Leave" : "Join"}
                                    </Button>
                                </ExpansionPanelActions>
                            }
                        </ExpansionPanel>
                    ))
                }

                <Dialog
                    open={this.state.showEditDialog}
                    onClose={this.closeEditDialog}>
                    <DialogTitle>Edit Study Group</DialogTitle>
                    <DialogContent className={classes.dialog}>
                        <StudyGroupForm
                            {...this.state.groupSelectedForEditing}
                            innerRef={this.studyGroupFormRef}
                            departments={this.props.departments}
                            submit={this.submitChildData}/>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => this.closeEditDialog()}>Cancel</Button>
                        <Button color="primary" onClick={() => this.submitGroupEdits()}>Submit</Button>
                    </DialogActions>
                </Dialog>
            </>
        );
    }
}

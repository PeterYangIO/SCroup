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

@withStyles(theme => ({
    column: {
        width: "33.3%"
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
            studyGroups: undefined,
            expandedPanel: -1
        }
    }

    async componentDidMount() {
        // await this.getData();

        // Dummy data
        const dummyData = [{id: 1, courseId: 1, ownerId: 1, capacity: 4, size: 3, location: "SAL 126", topic: 1, professor: "Jeffrey Miller, Ph.D", start: "2018-10-27T13:00:00", end: "2018-10-27T14:00:00"}, {id: 2, courseId: 1, ownerId: 1, capacity: 6, size: 6, location: "SAL 127", topic: 0, professor: "Jeffrey Miller, Ph.D", start: "2018-10-27T18:00:00", end: "2018-10-27T21:00:00"}];
        this.setState({
            studyGroups: dummyData
        });
    }

    getData = async () => {
        try {
            const response = await NetworkRequest.get("/api/study-groups", {
                courseId: this.props.course.id
            });
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

    handlePanel = (expandedPanel) => {
        this.setState({
            expandedPanel: this.state.expandedPanel === expandedPanel ? -1 : expandedPanel
        });
    };

    render() {
        const {classes, course} = this.props;

        if (course === undefined) {
            return (
                <Typography component="p" variant="body1">
                    Please select a course from the left to browse study groups.
                </Typography>
            )
        }
        return (
            <>
                <Typography component="h2" variant="h2">{course.department}-{course.number}</Typography>
                <Typography component="h5" variant="h5" gutterBottom>{course.name}</Typography>

                {
                    this.state.studyGroups.map(item => (
                        <ExpansionPanel
                            key={item.id}
                            expanded={this.state.expandedPanel === item.id}
                            onChange={() => this.handlePanel(item.id)}>
                            <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                <div className={classes.column}>
                                    <Typography
                                        className={`${classes.secondaryText} ${classes.inlineText}`}
                                        style={{marginRight: "1rem"}}>
                                        {item.size}/{item.capacity}
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
                            <ExpansionPanelActions>
                                <Button color="primary" size="small">
                                    Join
                                </Button>
                            </ExpansionPanelActions>
                        </ExpansionPanel>
                    ))
                }
            </>
        );
    }
}

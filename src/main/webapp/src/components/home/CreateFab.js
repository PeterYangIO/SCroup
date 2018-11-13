import React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button/Button";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog/Dialog";
import Tabs from "@material-ui/core/Tabs/Tabs";
import Tab from "@material-ui/core/Tab/Tab";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import NetworkRequest from "../../util/NetworkRequest";
import StudyGroupForm from "./StudyGroupForm";
import CourseForm from "./CourseForm";

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
    }
})
export default class CreateFab extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            open: false,
            tab: 0,
        };

        this.courseFormRef = React.createRef();
        this.studyGroupFormRef = React.createRef();
    }

    submitChildData = async (url, submitData) => {
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
        if (this.state.tab === 0) {
            await this.courseFormRef.current.submit();
        }
        else {
            await this.studyGroupFormRef.current.submit();
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
                            tab === 0 &&
                            <CourseForm
                                ref={this.courseFormRef}
                                submit={this.submitChildData}/>
                        }
                        {
                            tab === 1 &&
                            <StudyGroupForm
                                ref={this.studyGroupFormRef}
                                departments={this.props.departments}
                                submit={this.submitChildData}/>
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
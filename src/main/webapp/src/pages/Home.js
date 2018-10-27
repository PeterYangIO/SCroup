import React from "react";
import HeaderBar from "../components/common/HeaderBar";
import withStyles from "@material-ui/core/styles/withStyles";
import Paper from "@material-ui/core/Paper/Paper";
import Typography from "@material-ui/core/Typography/Typography";
import StudyGroups from "../components/home/StudyGroups";
import CreateFab from "../components/home/CreateFab";

@withStyles({
    container: {
        display: "flex",
        flexGrow: 1
    },
    courses: {
        flexGrow: 1,
        borderRadius: 0,
        padding: "1rem"
    },
    coursesTitle: {
        textAlign: "center"
    },
    groups: {
        flexGrow: 3,
        padding: "1rem"
    }
})
export default class Home extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            selectedCourse: undefined
        }
    }

    render() {
        const {classes} = this.props;
        return (
            <>
                <HeaderBar title="Home"/>
                <main className={classes.container}>
                    <Paper elevanation={2} className={classes.courses}>
                        <Typography
                            component="h4" variant="h4" gutterBottom
                            className={classes.coursesTitle}>
                            Courses
                        </Typography>
                    </Paper>
                    <section className={classes.groups}>
                        <StudyGroups courseId={this.state.selectedCourse}/>
                    </section>
                </main>

                <CreateFab/>
            </>
        );
    }
}
import React from "react";
import Typography from "@material-ui/core/Typography/Typography";

export default class StudyGroups extends React.Component {
    render() {
        const {courseId} = this.props;

        if (courseId === undefined) {
            return (
                <Typography component="p" variant="body1">
                    Please select a course from the left to browse study groups
                </Typography>
            )
        }
        return (
            <p>TODO</p>
        );
    }
}

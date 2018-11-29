import React from "react";
import { withStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";


const styles = theme => ({
    info: {
        maxWidth: 800,
        textAlign: 'center',
        marginBottom: 20,
        marginLeft: 20
    },
    centerText: {
        textAlign: 'center'
    }
});

class ClassList extends React.Component {

    render() {
        const { classes } = this.props;
        const uniqueClasses = [];

        return (
            <Card className={classes.info}>
                <div>
                    <Typography variant="h6">
                        Classes
                    </Typography>
                </div>
                <div>
                    <List>
                        {this.props.info && this.props.info.length > 0 && this.props.info.map(el => {
                            const className = `${el.department} ${el.courseNumber} - ${el.courseName}`;
                            if (uniqueClasses.includes(className)) {
                                return;
                            }
                            uniqueClasses.push(className);
                            return (
                                <ListItem dense key={el.id}>
                                    <ListItemText className={classes.centerText}>{className}</ListItemText>
                                </ListItem>
                            );
                        })}
                    </List>
                </div>
            </Card>
        );
    }
}


export default withStyles(styles)(ClassList);


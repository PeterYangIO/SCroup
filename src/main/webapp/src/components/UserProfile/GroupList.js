import React from "react";
import { withStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import {Link} from "react-router-dom";
import moment from "moment";


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

class GroupList extends React.Component {


    render() {
        const { classes } = this.props;

        return (
            <Card className={classes.info}>
                <div>
                    <Typography variant="h6">
                        Study Group
                    </Typography>
                </div>
                <div>
                    <List>
                        {this.props.info && this.props.info.length > 0 && this.props.info.map (el => {
                            return (
                                <ListItem dense button key={el.id} component={Link} to={`/dashboard/${el.id}`}>
                                    <ListItemText
                                        className={classes.centerText}
                                        primary={`${el.name}'s Group (${el.department} ${el.courseNumber})`}/>
                                </ListItem>
                            );
                        })}
                    </List>
                </div>
            </Card>
        );
    }
}


export default withStyles(styles)(GroupList);


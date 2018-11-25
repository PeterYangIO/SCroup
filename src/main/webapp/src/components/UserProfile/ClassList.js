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
        return (
            <Card className={classes.info}>
                <div>
                    <Typography variant="h6">
                        Classes
                    </Typography>
                </div>
                <div>
                    <List>
                        <ListItem dense button>
                            <ListItemText className={classes.centerText}>CSCI 201</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.centerText}>CSCI 270</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.centerText}>CSCI 356</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.centerText}>ITP 368</ListItemText>
                        </ListItem>
                    </List>
                </div>
            </Card>
        );
    }
}


export default withStyles(styles)(ClassList);


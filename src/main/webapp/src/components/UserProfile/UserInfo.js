import React from "react";
import { withStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import Avatar from "@material-ui/core/Avatar";
import Divider from "@material-ui/core/Divider";
import SchoolIcon from "@material-ui/icons/School";
import EmailIcon from "@material-ui/icons/Email";
import Grid from "@material-ui/core/Grid";


const styles = theme => ({
    user: {
        maxWidth: 400,
        textAlign: 'center'
    },
    bigAvatar: {
        width: 100,
        height: 100,
        marginLeft: 'auto',
        marginRight: 'auto',
        marginTop: 20,
        marginBottom: 20
    },
    textLeft: {
        textAlign: 'left'
    }
});

class UserInfo extends React.Component {
    render() {
        const { classes } = this.props;
        return (
            <Card className={classes.user}>
                <Avatar
                    className={classes.bigAvatar}
                    src="https://png.pngtree.com/svg/20170602/person_1058425.png"
                />
                <Divider />
                <CardContent>
                    <Typography gutterBottom variant="h5" component="h2">
                        Nhan Tran
                    </Typography>
                    <Grid container>
                        <Grid item sm={4}>
                            <SchoolIcon color="primary" />
                        </Grid>
                        <Grid item sm={8} className={classes.textLeft}>
                            <Typography component="p">University of Southern California</Typography>
                        </Grid>

                        <Grid item sm={4}>
                            <EmailIcon color="primary" />
                        </Grid>
                        <Grid item sm={8} className={classes.textLeft}>
                            <Typography component="p">trannhan@usc.edu</Typography>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        );
    }
}


export default withStyles(styles)(UserInfo);

